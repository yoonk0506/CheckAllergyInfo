package com.yoon.allergyinfo

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yoon.allergyinfo.composeUI.DropdownMenuButton
import com.yoon.allergyinfo.composeUI.SearchBar
import com.yoon.allergyinfo.composeUI.SearchState
import com.yoon.allergyinfo.data.AllergyData
import com.yoon.allergyinfo.data.SharedAllergyData
import com.yoon.allergyinfo.module.YLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class SearchDisplay {
    InitialResults, Suggestions, Results, NoResults
}

class MainActivity : AppCompatActivity() {

    private val mSharedAllergyData: SharedAllergyData by viewModels()

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tempStr = "음식,\n" +
                "LEVEL0,아메리카 치즈(노란색 낱개로 파는 치즈),LEVEL0,해바라기씨유\n" +
                "LEVEL0,체다치즈,LEVEL0,식물성 유지\n" +
                "LEVEL3,코티지치즈,LEVEL1,식용청색 제1호\n" +
                "LEVEL0,스위스치즈,LEVEL2,식용청색 제2호\n" +
                "LEVEL0,소 우유,LEVEL3,카라멜 색소\n" +
                "LEVEL1,염소 우유,LEVEL3,AC 적색 염료\n" +
                "LEVEL2,요거트(플레인 그릭),LEVEL3,황색 5호\n" +
                "LEVEL0,요거트(플레인),LEVEL3,선셋 옐로우· 황색 6호\n" +
                "LEVEL1,아몬드오일,LEVEL2,사과\n" +
                "LEVEL3,소 지방,LEVEL2,바나나\n" +
                "LEVEL3,캐놀라유,LEVEL3,블루베리\n" +
                "LEVEL0,닭의 지방,LEVEL0,캔터루프 멜론\n" +
                "LEVEL0,코코넛오일,LEVEL2,크랜베리\n" +
                "LEVEL0,대구 간유,LEVEL1,무화과\n" +
                "LEVEL3,대마씨유,LEVEL1,허니듀 멜론\n" +
                "LEVEL0,청어 오일,LEVEL3,키위\n" +
                "LEVEL0,올리브오일,LEVEL0,망고\n" +
                "LEVEL3,돼지의 지방,LEVEL1,오렌지\n" +
                "LEVEL1,연어오일,LEVEL2,복숭아\n" +
                "LEVEL0,정어리 기름,LEVEL0,배\n" +
                "LEVEL2,참기름,LEVEL0,파인애플\n" +
                "LEVEL2,콩기름,LEVEL0,라즈베리\n" +
                "LEVEL3,딸기,LEVEL1,밀 글루텐\n" +
                "LEVEL3,토마토박(토마토주스 또는 케첩을 만들 때 나오는 부산물),LEVEL0,밀 분쇄물\n" +
                "LEVEL1,수박,LEVEL3,통곡물 수수\n" +
                "LEVEL3,보리,LEVEL3,알팔파· 동물 사료용으로 재배됨\n" +
                "LEVEL0,양조용 쌀,LEVEL0,건조 알팔파(자주개자리)\n" +
                "LEVEL1,옥수수 가루,LEVEL0,보리새싹\n" +
                "LEVEL3,아마씨,LEVEL2,꿀벌의 화분\n" +
                "LEVEL0,옥수수(grain으로 분류되는 옥수수는 팝콘과 같이 옥수수알이 건조되거나 낱알로 가공된 형태),LEVEL0,맥주 효모\n" +
                "LEVEL1,조,LEVEL0,캣닙\n" +
                "LEVEL0,귀리,LEVEL3,치아씨드\n" +
                "LEVEL0,생알곡 귀리,LEVEL2,클로렐라\n" +
                "LEVEL0,정맥보리,LEVEL1,젤라틴\n" +
                "LEVEL0,퀴노아,LEVEL3,대마\n" +
                "LEVEL0,퀴노아 파우더,LEVEL0,켈프(다시마과에 속하는 해초)\n" +
                "LEVEL2,현미(갈색쌀),LEVEL1,락토바실러스 아시도필루스(유산균)\n" +
                "LEVEL2,백미,LEVEL1,콩 식이섬유\n" +
                "LEVEL0,쌀겨,LEVEL0,감자 가루\n" +
                "LEVEL2,쌀가루,LEVEL0,감자 전분\n" +
                "LEVEL3,호밀,LEVEL2,분말셀룰로오스\n" +
                "LEVEL1,콩가루,LEVEL3,차전차피\n" +
                "LEVEL1,분쇄대두(콩단백),LEVEL1,스피룰리나\n" +
                "LEVEL0,밀가루,LEVEL1,타피오카\n" +
                "LEVEL2,타피오카 전분,LEVEL0,혈액· 털· 발굽· 뿔을 제외한 소 조직을 의미하며 건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨\n" +
                "LEVEL0,사과사이다 식초,LEVEL1,소의 지방조직으로 제조한 지방· 우지\n" +
                "LEVEL3,이스트,LEVEL0,들소(버팔로) 고기\n" +
                "LEVEL3,효모 배양물,LEVEL2,혈액· 털· 발굽· 뿔을 제외한 들소 조직을 의미하며 건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨\n" +
                "LEVEL0,유카시데게라 추출물,LEVEL0,골분 (동물 뼈가루)\n" +
                "LEVEL2,아몬드,LEVEL0,캐놀라밀(카놀라 기름을 짜고 남은 찌꺼기)\n" +
                "LEVEL0,캐슈넛,LEVEL0,닭고기\n" +
                "LEVEL3,땅콩버터,LEVEL3,닭뼈로 끓인 물(육수를 의미)\n" +
                "LEVEL2,땅콩,LEVEL0,닭의 부산물(뼈· 껍데기· 깃털 등 포함)\n" +
                "LEVEL0,해바라기씨,LEVEL2,닭의 심장\n" +
                "LEVEL3,발효된 알팔파로 만든 사료 또는 비료,LEVEL3,닭의 간\n" +
                "LEVEL3,알팔파 농축물,LEVEL1,혈액· 깃털· 발톱을 제외한 닭의 조직을 의미하며 건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨\n" +
                "LEVEL1,악어고기,LEVEL3,오리 고기\n" +
                "LEVEL2,혈액을 제외한 모든 악어 조직을 의미하며· 건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨,LEVEL0,오리 모래주머니\n" +
                "LEVEL0,베이컨,LEVEL1,오리간\n" +
                "LEVEL2,소고기,LEVEL1,혈액· 깃털· 발톱을 제외한 오리의 조직을 의미하며 건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨\n" +
                "LEVEL2,소 뼈를 우린 물(사골· 육수를 의미),LEVEL0,흰자(닭)\n" +
                "LEVEL0,소 심장,LEVEL0,흰자(오리)\n" +
                "LEVEL0,소 가죽,LEVEL0,흰자(메추라기)\n" +
                "LEVEL0,육포,LEVEL0,노른자(닭)\n" +
                "LEVEL0,소 신장,LEVEL2,노른자(오리)\n" +
                "LEVEL0,소간,LEVEL0,노른자(메추라기)\n" +
                "LEVEL2,고라니,LEVEL0,농축 콩단백\n" +
                "LEVEL0,염소고기,LEVEL1,분리 콩단백\n" +
                "LEVEL1,거위고기,LEVEL1,대두박(콩을 분쇄하여 기름을 추출하고 남은 부산물)\n" +
                "LEVEL2,캥거루고기,LEVEL1,칠면조\n" +
                "LEVEL2,양고기,LEVEL0,칠면조 뼈로 만든 국물(육수를 의미)\n" +
                "LEVEL1,건조한 부분으로· 드라이 렌더링을 한 양고기· 대부분 사료에 많이 사용됨,LEVEL0,칠면조의 모래주머니\n" +
                "LEVEL0,완두콩 단백질,LEVEL1,칠면조의 심장\n" +
                "LEVEL2,꿩,LEVEL1,칠면조의 간\n" +
                "LEVEL1,꿩 모래주머니,LEVEL1,혈액· 깃털· 발톱을 제외한 칠면조의 조직을 의미 하며 건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨\n" +
                "LEVEL0,꿩의 심장,LEVEL0,송아지고기(어린소고기)\n" +
                "LEVEL1,꿩의 간,LEVEL0,사슴 고기\n" +
                "LEVEL2,돼지고기,LEVEL0,혈액· 털· 발굽· 뿔을 제외한 사슴 조직을 의미하며 건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨\n" +
                "LEVEL0,돼지 심장,LEVEL0,멸치\n" +
                "LEVEL1,돼지 신장,LEVEL2,간식이나 사료에 첨가되는 형태의 건조된 멸치 또는 분말(어분) 등\n" +
                "LEVEL0,돼지 간,LEVEL0,메기\n" +
                "LEVEL3,혈액· 발 등을 제외한 돼지 조직을 의미하며 건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨,LEVEL3,비늘을 제외한 메기조직\n" +
                "LEVEL0,돼지 껍질,LEVEL0,게\n" +
                "LEVEL0,메추라기,LEVEL1,생선뼈와 머리로 만든 국물 (육수를 의미)\n" +
                "LEVEL2,토끼,LEVEL0,혈액· 비늘을 제외한 생선을 의미하며 건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨\n" +
                "LEVEL0,토끼 심장,LEVEL0,넙치\n" +
                "LEVEL0,토끼 간,LEVEL3,청어\n" +
                "LEVEL0,혈액· 발 등을 제외한 토끼 조직을 의미하며 건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨,LEVEL3,간식이나 사료에 첨가되는 형태의 건조된 청어 또는 분말(어분) 등\n" +
                "LEVEL0,랍스타,LEVEL0,파슬리\n" +
                "LEVEL3,고등어,LEVEL2,파슬리 가루\n" +
                "LEVEL2,청어의 일종으로 혈액· 비늘을 제외한 생선을 의미하며 건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨,LEVEL0,로즈마리\n" +
                "LEVEL3,홍합,LEVEL3,로즈마리 추출물\n" +
                "LEVEL1,전체적인 해양 어류· 일반적으로 Fish Meal 등 어류가 포함되는 사료나 간식에 첨가되는 형 태,LEVEL0,세이지\n" +
                "LEVEL2,굴,LEVEL1,타임\n" +
                "LEVEL2,연어,LEVEL1,터메릭(울금· 강황)\n" +
                "LEVEL0,간식이나 사료에 첨가되는 형태의 건조된 연어 또는 분말(어분) 등,LEVEL3,옥수수 시럽\n" +
                "LEVEL2,정어리,LEVEL1,꿀\n" +
                "LEVEL0,간식이나 사료에 첨가되는 형태의 건조된 정어리 또는 분말(어분) 등,LEVEL0,자당\n" +
                "LEVEL2,농어,LEVEL2,황설탕\n" +
                "LEVEL0,새우,LEVEL3,한천\n" +
                "LEVEL0,서대,LEVEL0,아스코르브 산\n" +
                "LEVEL0,틸라피아 민물고기,LEVEL0,비오틴\n" +
                "LEVEL0,송어(바다),LEVEL0,탄산 칼슘(사료에 방부재· 색상보존제· 칼슘의 저 렴한 공급원으로 사용됨)\n" +
                "LEVEL1,참치,LEVEL0,염화칼슘(식품의 경화제로 많이 사용되며· 과일 과 채소의 탄력을 향상시키고 칼슘 보충제 및 많 은 제품에 사용된다)\n" +
                "LEVEL2,흰살 생선 가공 시 남은 생선살· 뼈· 찌꺼기로 만든 것 .건조시킨 분쇄육을 의미함. 대부분 사료에 많이 사용됨,LEVEL2,칼슘 요오드\n" +
                "LEVEL3,바질,LEVEL0,칼슘 판토텐(비타민 B5또는 판토텐산의 공급원)\n" +
                "LEVEL2,고수,LEVEL0,황산칼슘\n" +
                "LEVEL2,페뉴그릭씨드(호로파)· 향신료로 사용,LEVEL0,카르니틴\n" +
                "LEVEL2,생강,LEVEL0,카라기난\n" +
                "LEVEL2,오레가노 분말,LEVEL0,염화 콜린\n" +
                "LEVEL0,시트릭산,LEVEL3,황산아연\n" +
                "LEVEL3,구리황산염,LEVEL2,천연 토코페롤(비타민E)· 산화방지제\n" +
                "LEVEL2,인산이칼슘(사료에 포함됨),LEVEL1,알파 토코페롤(토코페롤)· 산화방지제\n" +
                "LEVEL0,황산철,LEVEL0,감마 토코페롤(토코페롤)· 산화방지제\n" +
                "LEVEL0,엽산,LEVEL2,델타 토코페롤(토코페롤)\n" +
                "LEVEL0,글루코사민 염산염,LEVEL1,아스파라거스\n" +
                "LEVEL0,구아검(식품첨가물),LEVEL1,검은콩\n" +
                "LEVEL2,이노시톨(동식물에 의해 합성된 알코올),LEVEL1,병아리콩\n" +
                "LEVEL0,이눌린(치커리에서 추출됨),LEVEL1,그린빈\n" +
                "LEVEL0,레시틴,LEVEL1,사탕무· 비트\n" +
                "LEVEL3,망간 단백질(항산화제),LEVEL3,청경채\n" +
                "LEVEL1,황산 망간,LEVEL3,브로콜리\n" +
                "LEVEL3,니아신(비타민 B3의 형태),LEVEL2,방울다다기양배추\n" +
                "LEVEL2,오메가3 지방산,LEVEL0,양배추\n" +
                "LEVEL1,인산(유화제· 변색방지제),LEVEL0,당근\n" +
                "LEVEL0,염화 칼륨,LEVEL0,컬리플라워\n" +
                "LEVEL2,피리독신 염산염,LEVEL0,셀러리\n" +
                "LEVEL0,리보플라빈,LEVEL1,치커리뿌리\n" +
                "LEVEL1,염화 나트륨,LEVEL0,치커리 뿌리 추출물\n" +
                "LEVEL1,소디움 셀레네이트(무기화합물),LEVEL1,옥수수(Vegetable로 분류되는 옥수수대에 붙어 있는 신선한 옥수수 그대로를 의미)\n" +
                "LEVEL1,콩레시틴,LEVEL2,오이\n" +
                "LEVEL3,타우린,LEVEL1,민들레의 연한 잎\n" +
                "LEVEL1,콜라드그린,LEVEL0,맷돌(늙은) 호박(할로윈 데이에 장식으로 많이 쓰 이는 주황색의 큰 호박)\n" +
                "LEVEL0,렌틸콩,LEVEL3,시금치\n" +
                "LEVEL0,완두콩,LEVEL2,버터넛 호박(단호박과 비슷한 맛에 씨가 적고 수 프를 끓이기에 좋은 호박)\n" +
                "LEVEL1,노란감자,LEVEL0,주키니 호박(애호박과 비슷한 호박)\n" +
                "LEVEL3,고구마,LEVEL3,얌\n" +
                "LEVEL0,흰감자\n" +
                "환경,\n" +
                "LEVEL0,아크릴천,LEVEL0,클로버\n" +
                "LEVEL0,알로에베라,LEVEL0,코카마이도프로필베타인\n" +
                "LEVEL2,아미트라즈(살충제 농약),LEVEL3,바퀴벌레\n" +
                "LEVEL3,암모니아,LEVEL1,카필라리스 이삭\n" +
                "LEVEL1,암모늄라우레스설페이트(계면활성제),LEVEL1,면\n" +
                "LEVEL0,개미,LEVEL0,바랭이(잡초의 일종)\n" +
                "LEVEL2,사과식초,LEVEL3,민들레\n" +
                "LEVEL1,사과나무,LEVEL2,수영\n" +
                "LEVEL0,유럽산 물푸레나무,LEVEL0,개· 강아지의 비듬(각질)\n" +
                "LEVEL0,사시나무,LEVEL3,오리깃털\n" +
                "LEVEL2,아스페르길루스 푸미가투스(흙속에 존재하는 곰팡이),LEVEL2,느릅나무\n" +
                "LEVEL0,베이킹소다,LEVEL2,페스큐(다년생 목초)\n" +
                "LEVEL2,대나무,LEVEL3,피프로닐\n" +
                "LEVEL3,벌,LEVEL2,분홍바늘꽃\n" +
                "LEVEL3,너도밤나무,LEVEL1,벼룩\n" +
                "LEVEL0,우산잔디,LEVEL0,양털(플리스)\n" +
                "LEVEL0,부톡시에탄올,LEVEL0,포름알데이드\n" +
                "LEVEL0,미나리아재비,LEVEL0,글리세린\n" +
                "LEVEL3,하이포아염소산칼슘(염화칼슘· 수산화칼슘· 탄산칼슘 등을 포함),LEVEL0,미역취\n" +
                "LEVEL3,고양이 비듬(각질),LEVEL3,거위 깃털\n" +
                "LEVEL2,체리나무,LEVEL2,산사나무\n" +
                "LEVEL0,염소,LEVEL3,말파리· 쇠등에\n" +
                "LEVEL0,말,LEVEL0,참나무\n" +
                "LEVEL0,집먼지진드기,LEVEL0,오리새 식물\n" +
                "LEVEL0,과산화수소,LEVEL3,팬지\n" +
                "LEVEL3,이미다클로프리드(살충제로 사용되는 독성 물 질),LEVEL0,파라벤\n" +
                "LEVEL1,이소프로필알코올,LEVEL2,배나무\n" +
                "LEVEL1,쟈스민,LEVEL0,퍼클로로에틸렌(드라이클리닝에 많이 사용됨)\n" +
                "LEVEL2,주니퍼 부시,LEVEL2,페르메트린(살충제· 구충제· 방충제로 사용됨)\n" +
                "LEVEL0,볏과 식물(유럽 원산지),LEVEL0,프탈레이트\n" +
                "LEVEL2,왕포아풀,LEVEL1,비둘기 똥\n" +
                "LEVEL3,가죽,LEVEL0,비둘기\n" +
                "LEVEL2,목련,LEVEL0,소나무\n" +
                "LEVEL0,맥아식초,LEVEL0,폴리에스테르\n" +
                "LEVEL3,단풍나무,LEVEL0,폴리에틸렌 글리콜(피복약· 연고기제· 용제에 속함)\n" +
                "LEVEL2,큰뚝새풀,LEVEL1,폴리소르베이트(계면활성제로 많이 사용됨)\n" +
                "LEVEL0,메토프렌(유약 호르몬),LEVEL2,사시나무(포풀러스)\n" +
                "LEVEL2,메칠파라벤(화장품의 살균보존제· 방부제),LEVEL0,쥐똥나무\n" +
                "LEVEL1,쥐,LEVEL1,프로필렌 글리콜(무색 투명한 시럽상의 액체)\n" +
                "LEVEL3,쥐똥,LEVEL1,양전하를 가진 세제(샴푸나 컨디셔너)\n" +
                "LEVEL3,미네랄오일,LEVEL1,토끼\n" +
                "LEVEL0,곰팡이I알러젠,LEVEL0,돼지풀\n" +
                "LEVEL3,모기,LEVEL3,시궁쥐 똥\n" +
                "LEVEL1,나일론,LEVEL0,시궁쥐\n" +
                "LEVEL0,장미,LEVEL3,진드기\n" +
                "LEVEL0,호밀풀속,LEVEL2,트리클로산\n" +
                "LEVEL0,가성소다,LEVEL3,튤립\n" +
                "LEVEL2,하이포아염소산나트륨(계면제의 일종),LEVEL2,말벌\n" +
                "LEVEL0,소듐라우레스설페이트(계면제의 일종),LEVEL0,백식초\n" +
                "LEVEL0,소듐라우릴설페이트(계면제의 일종),LEVEL0,버드나무\n" +
                "LEVEL0,독일가문비 나무,LEVEL0,울\n" +
                "LEVEL0,세인트어거스틴그래스,LEVEL0,잔디"

        createData(tempStr)

        setContent {
            val state: SearchState = rememberSearchState()
            val list by mSharedAllergyData.allergy.collectAsState()
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = colorResource(id = R.color.white)
            ) {
                val options = arrayListOf("높은순", "낮은순")
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(colorResource(id = R.color.transparent))
                ) {
                    SearchBar(
                        query = state.query,
                        onQueryChange = {
                            state.query = it
                            mSharedAllergyData.getSearchData(state.query.text)
                        },
                        onSearchFocusChange = { state.focused = it },
                        onClearQuery = {
                            state.query = TextFieldValue("")
                            mSharedAllergyData.getSearchData("")
                        },
                        onBack = {
                            state.query = TextFieldValue("")
                            mSharedAllergyData.getSearchData("")
                        },
                        searching = state.searching,
                        focused = state.focused,
                        modifier = Modifier.background(colorResource(id = R.color.white)),
                    )

                    LaunchedEffect(state.query.text) {
                        state.searching = true
                        delay(100)
                        state.searchResults = list
                        state.searching = false
                    }

                    DropdownMenuButton(
                        options = options,
                        selectedIndex = mSharedAllergyData.sortIndex,
                        onOptionSelected = { index ->
                            CoroutineScope(Dispatchers.IO).launch {
                                YLog.em("ㅁㅁㅁㅁ ㄷ ㄷ ㄷ ㄷ ㄷ");
                                mSharedAllergyData.sortIndex = index
                                mSharedAllergyData.updateData()
                            }
                        },
                        modifier = Modifier.align(alignment = Alignment.End)
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        content = {
                            itemsIndexed(list) { _, data ->
                                addContent(data)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun createData(dataStr: String) {
        val tempStr = dataStr.replace(",L", "\nL")
        val splitDataStr = tempStr.split("\n")
        var classification = ""
        val allergyDataList = ArrayList<AllergyData>()
        for (str in splitDataStr) {
            val tempDataStr = str.split(",")
            if (str.contains("음식") || str.contains("환경")) {
                classification = tempDataStr[0]
                continue
            }
            allergyDataList.add(AllergyData(classification, tempDataStr[0], tempDataStr[1]))
            YLog.em("추가 데이터 // 분류 : $classification // key: ${tempDataStr[0]} // data: ${tempDataStr[1]}")
        }
        mSharedAllergyData.dataList = allergyDataList
        mSharedAllergyData.updateData()
    }

    @Preview
    @Composable
    private fun addContent(data: AllergyData) {
        var isExpandedMemo by remember { mutableStateOf(false) }
        val bgColor = when (data.level) {
            "LEVEL3" -> R.color.level3
            "LEVEL2" -> R.color.level2
            "LEVEL1" -> R.color.level1
            else -> R.color.level0
        }
        val tagColor = when (data.classification) {
            "음식" -> R.color.food
            "환경" -> R.color.environment
            else -> R.color.gray
        }
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .clickable { isExpandedMemo = !isExpandedMemo }
                .height(if (isExpandedMemo) 100.dp else 50.dp)
                .fillMaxWidth()
                .background(colorResource(id = bgColor))
        ) {
            if (isExpandedMemo) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .fillMaxHeight()
                        .background(colorResource(id = tagColor))
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = data.name,
                maxLines = if (isExpandedMemo) Int.MAX_VALUE else 1,
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h4.copy(
                    shadow = Shadow(
                        color = colorResource(id = R.color.black30),
                        offset = Offset(4f, 4f),
                        blurRadius = 8f
                    ),
                    fontSize = 20.sp
                )
            )
        }
    }

    @Composable
    fun rememberSearchState(
        query: TextFieldValue = TextFieldValue(""),
        focused: Boolean = false,
        searching: Boolean = false,
        suggestions: List<AllergyData> = emptyList(),
        searchResults: List<AllergyData> = emptyList()
    ): SearchState {
        return remember {
            SearchState(
                query = query,
                focused = focused,
                searching = searching,
                suggestions = suggestions,
                searchResults = searchResults
            )
        }
    }
}