package com.lt.http;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.lt.entity.CapitalInfo;
import com.lt.entity.ClinchDetail;
import com.lt.utils.Constants;
import com.lt.utils.RealCodeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author gaijf
 * @description
 * @date 2019/9/19
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpTest {

    String codes ="sh600617,sh600619,sh600620,sh600621,sh600623,sh600624,sh600628,sh600629,sh600635,sh600636,sh600637,sh600639,sh600642,sh600643,sh600644,sh600645,sh600648,sh600649,sh600650,sh600652,sh600653,sh600654,sh600657,sh600658,sh600660,sh600661,sh600662,sh600663,sh600664,sh600665,sh600668,sh600671,sh600673,sh600674,sh600675,sh600676,sh600678,sh600679,sh600681,sh600682,sh600683,sh600684,sh600685,sh600686,sh600687,sh600688,sh600689,sh600690,sh600691,sh600692,sh600693,sh600695,sh600696,sh600697,sh600698,sh600699,sh600701,sh600704,sh600705,sh600707,sh600708,sh600710,sh600711,sh600713,sh600714,sh600716,sh600717,sh600718,sh600719,sh600720,sh600721,sh600722,sh600723,sh600724,sh600725,sh600726,sh600727,sh600728,sh600729,sh600731,sh600733,sh600734,sh600735,sh600736,sh600737,sh600738,sh600739,sh600740,sh600741,sh600744,sh600746,sh600748,sh600750,sh600751,sh600753,sh600754,sh600755,sh600756,sh600757,sh600758,sh600760,sh600761,sh600763,sh600765,sh600767,sh600768,sh600770,sh600771,sh600773,sh600774,sh600775,sh600776,sh600777,sh600778,sh600779,sh600780,sh600782,sh600783,sh600784,sh600785,sh600787,sh600789,sh600790,sh600791,sh600792,sh600793,sh600795,sh600796,sh600798,sh600800,sh600801,sh600802,sh600803,sh600804,sh600805,sh600807,sh600809,sh600810,sh600811,sh600814,sh600815,sh600816,sh600817,sh600818,sh600819,sh600820,sh600821,sh600822,sh600823,sh600824,sh600825,sh600827,sh600828,sh600830,sh600834,sh600835,sh600836,sh600837,sh600838,sh600839,sh600843,sh600844,sh600845,sh600846,sh600848,sh600850,sh600851,sh600853,sh600855,sh600856,sh600857,sh600858,sh600859,sh600861,sh600863,sh600865,sh600866,sh600867,sh600868,sh600869,sh600870,sh600872,sh600873,sh600874,sh600875,sh600876,sh600877,sh600879,sh600880,sh600881,sh600886,sh600887,sh600888,sh600889,sh600891,sh600892,sh600893,sh600894,sh600895,sh600897,sh600898,sh600900,sh600901,sh600908,sh600909,sh600919,sh600926,sh600928,sh600933,sh600936,sh600939,sh600958,sh600959,sh600960,sh600961,sh600962,sh600963,sh600965,sh600966,sh600967,sh600969,sh600970,sh600971,sh600973,sh600975,sh600976,sh600977,sh600978,sh600979,sh600980,sh600981,sh600982,sh600983,sh600985,sh600986,sh600987,sh600988,sh600990,sh600992,sh600993,sh600995,sh600997,sh600999,sh601000,sh601001,sh601002,sh601005,sh601006,sh601007,sh601008,sh601009,sh601010,sh601011,sh601012,sh601015,sh601016,sh601018,sh601019,sh601020,sh601021,sh601028,sh601038,sh601058,sh601066,sh601068,sh601069,sh601086,sh601088,sh601098,sh601099,sh601100,sh601101,sh601106,sh601107,sh601108,sh601111,sh601113,sh601117,sh601118,sh601126,sh601127,sh601128,sh601138,sh601139,sh601158,sh601162,sh601166,sh601168,sh601169,sh601179,sh601186,sh601188,sh601198,sh601199,sh601208,sh601211,sh601212,sh601216,sh601218,sh601222,sh601225,sh601226,sh601228,sh601229,sh601233,sh601288,sh601298,sh601311,sh601318,sh601319,sh601326,sh601328,sh601333,sh601336,sh601339,sh601368,sh601375,sh601377,sh601388,sh601390,sh601398,sh601500,sh601515,sh601518,sh601555,sh601558,sh601566,sh601567,sh601577,sh601579,sh601588,sh601598,sh601599,sh601600,sh601601,sh601606,sh601607,sh601608,sh601611,sh601616,sh601618,sh601619,sh601628,sh601633,sh601636,sh601666,sh601668,sh601669,sh601677,sh601678,sh601688,sh601689,sh601699,sh601700,sh601718,sh601727,sh601766,sh601777,sh601788,sh601789,sh601798,sh601800,sh601801,sh601811,sh601818,sh601828,sh601838,sh601857,sh601858,sh601865,sh601866,sh601877,sh601878,sh601880,sh601881,sh601882,sh601886,sh601888,sh601890,sh601898,sh601899,sh601900,sh601901,sh601908,sh601918,sh601919,sh601928,sh601929,sh601933,sh601939,sh601949,sh601952,sh601958,sh601966,sh601969,sh601975,sh601985,sh601988,sh601989,sh601991" +
            "sh601992,sh601996,sh601997,sh601998,sh601999,sh603001,sh603002,sh603003,sh603006,sh603008,sh603009,sh603010,sh603011,sh603013,sh603015,sh603017,sh603018,sh603019,sh603020,sh603021,sh603022,sh603023,sh603025,sh603026,sh603027,sh603028,sh603029,sh603030,sh603032,sh603035,sh603036,sh603037,sh603038,sh603039,sh603040,sh603041,sh603043,sh603045,sh603055,sh603056,sh603058,sh603059,sh603060,sh603063,sh603066,sh603067,sh603069,sh603076,sh603077,sh603079,sh603080,sh603081,sh603083,sh603085,sh603086,sh603088,sh603090,sh603096,sh603098,sh603099,sh603100,sh603103,sh603108,sh603111,sh603113,sh603116,sh603117,sh603123,sh603128,sh603131,sh603133,sh603139,sh603156,sh603158,sh603166,sh603167,sh603169,sh603177,sh603178,sh603179,sh603181,sh603186,sh603187,sh603188,sh603192,sh603196,sh603200,sh603208,sh603214,sh603220,sh603222,sh603223,sh603225,sh603226,sh603227,sh603229,sh603232,sh603238,sh603239,sh603258,sh603259,sh603260,sh603268,sh603269,sh603278,sh603283,sh603286,sh603289,sh603298,sh603303,sh603305,sh603308,sh603309,sh603315,sh603316,sh603318,sh603319,sh603320,sh603321,sh603322,sh603323,sh603326,sh603329,sh603331,sh603332,sh603333,sh603335,sh603336,sh603339,sh603348,sh603357,sh603358,sh603359,sh603360,sh603363,sh603365,sh603366,sh603367,sh603380,sh603383,sh603387,sh603388,sh603389,sh603396,sh603398,sh603399,sh603416,sh603429,sh603458,sh603486,sh603488,sh603496,sh603499,sh603500,sh603506,sh603507,sh603508,sh603515,sh603516,sh603518,sh603519,sh603527,sh603528,sh603533,sh603535,sh603536,sh603555,sh603556,sh603557,sh603558,sh603567,sh603568,sh603569,sh603578,sh603579,sh603580,sh603583,sh603585,sh603587,sh603588,sh603589,sh603590,sh603595,sh603598,sh603599,sh603601,sh603602,sh603605,sh603606,sh603607,sh603609,sh603611,sh603612,sh603615,sh603616,sh603617,sh603618,sh603628,sh603629,sh603630,sh603633,sh603637,sh603639,sh603648,sh603650,sh603655,sh603657,sh603658,sh603659,sh603660,sh603661,sh603663,sh603665,sh603667,sh603668,sh603669,sh603676,sh603678,sh603679,sh603680,sh603683,sh603686,sh603689,sh603693,sh603696,sh603699,sh603700,sh603703,sh603707,sh603708,sh603709,sh603712,sh603717,sh603718,sh603722,sh603725,sh603729,sh603730,sh603733,sh603739,sh603757,sh603758,sh603766,sh603768,sh603777,sh603779,sh603789,sh603797,sh603799,sh603800,sh603801,sh603808,sh603813,sh603816,sh603817,sh603819,sh603823,sh603825,sh603826,sh603829,sh603833,sh603838,sh603839,sh603848,sh603855,sh603856,sh603858,sh603860,sh603861,sh603866,sh603869,sh603871,sh603878,sh603879,sh603881,sh603883,sh603885,sh603886,sh603887,sh603889,sh603895,sh603896,sh603897,sh603898,sh603899,sh603901,sh603906,sh603908,sh603909,sh603916,sh603922,sh603928,sh603933,sh603937,sh603939,sh603955,sh603958,sh603959,sh603966,sh603968,sh603970,sh603976,sh603977,sh603979,sh603985,sh603986,sh603987,sh603988,sh603989,sh603990,sh603991,sh603993,sh603996,sh603997,sh603998,sh603999,sz000002,sz000005,sz000006,sz000007,sz000008,sz000009,sz000010,sz000011,sz000014,sz000016,sz000017,sz000019,sz000020,sz000023,sz000026,sz000027,sz000028,sz000035,sz000036,sz000037,sz000039,sz000040,sz000042,sz000043,sz000045,sz000046,sz000048,sz000055,sz000059,sz000060,sz000061,sz000062,sz000065,sz000069,sz000078,sz000088,sz000089,sz000090,sz000099,sz000151,sz000155,sz000156,sz000157,sz000158,sz000159,sz000166,sz000301,sz000333,sz000338,sz000400,sz000402,sz000403,sz000404,sz000407,sz000408,sz000409,sz000415,sz000416,sz000417,sz000419,sz000420,sz000421,sz000423,sz000425,sz000426,sz000429,sz000498,sz000501,sz000502,sz000505,sz000507,sz000509,sz000510,sz000513,sz000514,sz000516,sz000517,sz000518,sz000520,sz000521,sz000523,sz000525,sz000526,sz000528" +
            "sz000529,sz000530,sz000531,sz000532,sz000533,sz000538,sz000539,sz000540,sz000541,sz000543,sz000545,sz000546,sz000548,sz000551,sz000552,sz000553,sz000555,sz000557,sz000559,sz000560,sz000561,sz000563,sz000565,sz000567,sz000570,sz000571,sz000573,sz000576,sz000581,sz000582,sz000585,sz000589,sz000590,sz000591,sz000592,sz000593,sz000596,sz000597,sz000598,sz000600,sz000605,sz000606,sz000607,sz000608,sz000609,sz000612,sz000615,sz000616,sz000617,sz000619,sz000620,sz000622,sz000623,sz000625,sz000627,sz000629,sz000630,sz000631,sz000633,sz000635,sz000636,sz000637,sz000638,sz000639,sz000650,sz000651,sz000652,sz000655,sz000657,sz000659,sz000661,sz000663,sz000665,sz000666,sz000667,sz000668,sz000669,sz000670,sz000671,sz000672,sz000673,sz000676,sz000677,sz000678,sz000679,sz000680,sz000683,sz000685,sz000686,sz000688,sz000690,sz000691,sz000697,sz000698,sz000700,sz000701,sz000702,sz000703,sz000705,sz000708,sz000709,sz000710,sz000712,sz000713,sz000715,sz000716,sz000717,sz000718,sz000719,sz000720,sz000722,sz000723,sz000725,sz000726,sz000727,sz000728,sz000729,sz000731,sz000733,sz000735,sz000736,sz000738,sz000750,sz000751,sz000753,sz000755,sz000756,sz000757,sz000758,sz000759,sz000760,sz000761,sz000762,sz000766,sz000767,sz000768,sz000776,sz000777,sz000778,sz000779,sz000780,sz000782,sz000783,sz000785,sz000786,sz000788,sz000789,sz000790,sz000791,sz000792,sz000793,sz000795,sz000797,sz000798,sz000800,sz000802,sz000803,sz000806,sz000807,sz000810,sz000811,sz000812,sz000813,sz000816,sz000818,sz000819,sz000820,sz000821,sz000822,sz000825,sz000826,sz000828,sz000829,sz000830,sz000831,sz000833,sz000835,sz000836,sz000837,sz000838,sz000839,sz000848,sz000850,sz000851,sz000852,sz000858,sz000859,sz000860,sz000861,sz000862,sz000863,sz000869,sz000875,sz000876,sz000877,sz000878,sz000880,sz000881,sz000882,sz000883,sz000885,sz000887,sz000889,sz000890,sz000892,sz000893,sz000895,sz000897,sz000898,sz000899,sz000900,sz000901,sz000902,sz000903,sz000905,sz000906,sz000908,sz000909,sz000910,sz000911,sz000912,sz000913,sz000915,sz000917,sz000919,sz000920,sz000921,sz000922,sz000923,sz000925,sz000926,sz000927,sz000928,sz000930,sz000932,sz000933,sz000935,sz000936,sz000937,sz000949,sz000950,sz000951,sz000952,sz000955,sz000957,sz000959,sz000960,sz000961,sz000963,sz000965,sz000966,sz000967,sz000968,sz000969,sz000970,sz000971,sz000972,sz000973,sz000980,sz000981,sz000983,sz000985,sz000987,sz000988,sz000989,sz000990,sz000993,sz000997,sz000998,sz000999,sz001696,sz001872,sz001896,sz001965,sz001979,sz002002,sz002003,sz002004,sz002005,sz002009,sz002010,sz002011,sz002012,sz002013,sz002014,sz002017,sz002019,sz002021,sz002022,sz002024,sz002025,sz002026,sz002027,sz002029,sz002030,sz002031,sz002032,sz002034,sz002037,sz002038,sz002039,sz002040,sz002041,sz002042,sz002043,sz002044,sz002045,sz002046,sz002047,sz002048,sz002049,sz002051,sz002053,sz002054,sz002057,sz002058,sz002060,sz002061,sz002062,sz002064,sz002065,sz002066,sz002067,sz002068,sz002069,sz002071,sz002075,sz002076,sz002077,sz002080,sz002083,sz002084,sz002085,sz002087,sz002088,sz002090,sz002091,sz002092,sz002094,sz002095,sz002096,sz002097,sz002099,sz002100,sz002101,sz002102,sz002104,sz002107,sz002108,sz002110,sz002111,sz002112,sz002113,sz002114,sz002115,sz002116,sz002117,sz002119,sz002120,sz002121,sz002124,sz002125,sz002126,sz002128,sz002130,sz002131,sz002132,sz002133,sz002135,sz002136,sz002137,sz002139,sz002140,sz002141,sz002142,sz002144,sz002145,sz002146,sz002147,sz002149,sz002150,sz002152,sz002153,sz002154,sz002155,sz002162,sz002163,sz002165,sz002166,sz002167,sz002168,sz002169,sz002170,sz002171,sz002173,sz002174,sz002177,sz002178,sz002179,sz002181" +
            "sz002182,sz002183,sz002184,sz002187,sz002188,sz002191,sz002196,sz002197,sz002198,sz002200,sz002202,sz002203,sz002204,sz002205,sz002206,sz002207,sz002208,sz002209,sz002210,sz002211,sz002212,sz002214,sz002215,sz002218,sz002219,sz002220,sz002221,sz002223,sz002225,sz002226,sz002227,sz002228,sz002229,sz002230,sz002232,sz002233,sz002234,sz002235,sz002237,sz002238,sz002239,sz002240,sz002241,sz002242,sz002243,sz002244,sz002245,sz002246,sz002247,sz002248,sz002249,sz002251,sz002252,sz002253,sz002254,sz002258,sz002259,sz002261,sz002262,sz002263,sz002264,sz002265,sz002266,sz002267,sz002268,sz002269,sz002270,sz002271,sz002272,sz002273,sz002275,sz002276,sz002277,sz002278,sz002279,sz002280,sz002281,sz002282,sz002283,sz002285,sz002286,sz002287,sz002289,sz002290,sz002291,sz002292,sz002293,sz002294,sz002295,sz002297,sz002299,sz002300,sz002302,sz002303,sz002304,sz002305,sz002306,sz002308,sz002309,sz002310,sz002312,sz002313,sz002314,sz002318,sz002319,sz002320,sz002321,sz002322,sz002325,sz002327,sz002328,sz002329,sz002330,sz002332,sz002333,sz002334,sz002335,sz002337,sz002340,sz002341,sz002344,sz002345,sz002346,sz002347,sz002349,sz002350,sz002351,sz002352,sz002353,sz002357,sz002358,sz002359,sz002360,sz002361,sz002364,sz002366,sz002367,sz002368,sz002369,sz002370,sz002374,sz002377,sz002380,sz002381,sz002382,sz002383,sz002386,sz002389,sz002391,sz002392,sz002393,sz002394,sz002395,sz002397,sz002398,sz002399,sz002400,sz002402,sz002403,sz002408,sz002410,sz002412,sz002413,sz002414,sz002416,sz002418,sz002419,sz002423,sz002425,sz002426,sz002427,sz002429,sz002430,sz002431,sz002432,sz002433,sz002434,sz002435,sz002437,sz002438,sz002439,sz002440,sz002441,sz002442,sz002443,sz002445,sz002448,sz002451,sz002454,sz002455,sz002457,sz002458,sz002461,sz002462,sz002463,sz002467,sz002468,sz002469,sz002470,sz002471,sz002472,sz002473,sz002474,sz002475,sz002477,sz002480,sz002482,sz002483,sz002484,sz002485,sz002488,sz002489,sz002490,sz002492,sz002493,sz002494,sz002495,sz002496,sz002497,sz002498,sz002499,sz002500,sz002503,sz002504,sz002505,sz002506,sz002507,sz002510,sz002511,sz002512,sz002514,sz002516,sz002518,sz002519,sz002520,sz002521,sz002522,sz002523,sz002526,sz002527,sz002528,sz002529,sz002530,sz002531,sz002532,sz002533,sz002534,sz002535,sz002536,sz002537,sz002538,sz002539,sz002543,sz002544,sz002545,sz002547,sz002548,sz002549,sz002550,sz002551,sz002554,sz002555,sz002558,sz002559,sz002560,sz002561,sz002562,sz002563,sz002564,sz002566,sz002568,sz002569,sz002571,sz002572,sz002573,sz002574,sz002576,sz002577,sz002578,sz002580,sz002581,sz002583,sz002585,sz002586,sz002588,sz002589,sz002591,sz002592,sz002593,sz002594,sz002595,sz002596,sz002597,sz002598,sz002599,sz002601,sz002602,sz002603,sz002605,sz002606,sz002607,sz002608,sz002609,sz002610,sz002612,sz002614,sz002616,sz002619,sz002620,sz002621,sz002623,sz002624,sz002625,sz002626,sz002627,sz002629,sz002630,sz002632,sz002637,sz002639,sz002644,sz002645,sz002649,sz002651,sz002652,sz002653,sz002656,sz002658,sz002659,sz002661,sz002662,sz002665,sz002666,sz002667,sz002668,sz002670,sz002672,sz002673,sz002674,sz002675,sz002677,sz002678,sz002679,sz002685,sz002687,sz002688,sz002689,sz002691,sz002693,sz002694,sz002695,sz002696,sz002699,sz002701,sz002703,sz002705,sz002708,sz002709,sz002713,sz002716,sz002717,sz002719,sz002721,sz002725,sz002726,sz002727,sz002728,sz002730,sz002732,sz002734,sz002735,sz002736,sz002737,sz002738,sz002739,sz002740,sz002743,sz002745,sz002746,sz002747,sz002748,sz002749,sz002753,sz002756,sz002757,sz002758,sz002759,sz002760,sz002761,sz002763,sz002766,sz002767,sz002768,sz002769,sz002771,sz002772,sz002773,sz002775,sz002776,sz002778" +
            "sz002779,sz002780,sz002782,sz002787,sz002788,sz002789,sz002791,sz002792,sz002795,sz002796,sz002797,sz002798,sz002799,sz002800,sz002801,sz002802,sz002803,sz002805,sz002806,sz002807,sz002808,sz002810,sz002811,sz002816,sz002818,sz002821,sz002822,sz002823,sz002825,sz002826,sz002827,sz002829,sz002830,sz002831,sz002832,sz002833,sz002835,sz002836,sz002839,sz002840,sz002841,sz002842,sz002843,sz002846,sz002849,sz002850,sz002851,sz002852,sz002853,sz002857,sz002858,sz002859,sz002860,sz002861,sz002862,sz002863,sz002864,sz002865,sz002867,sz002868,sz002870,sz002871,sz002872,sz002873,sz002875,sz002877,sz002878,sz002879,sz002880,sz002881,sz002882,sz002883,sz002884,sz002890,sz002891,sz002893,sz002895,sz002896,sz002898,sz002900,sz002901,sz002902,sz002903,sz002906,sz002907,sz002910,sz002912,sz002913,sz002919,sz002920,sz002921,sz002922,sz002923,sz002925,sz002926,sz002928,sz002929,sz002930,sz002932,sz002933,sz002936,sz002937,sz002938,sz002939,sz002943,sz002945,sz002946,sz002948";

    @Autowired
    private RestTemplate restTemplate;
    private CountDownLatch latch = null;
    private static final ThreadPoolExecutor excutor = new ThreadPoolExecutor(2,8,20,TimeUnit.SECONDS,new LinkedBlockingDeque<>(3000));

    @Test
    public void startTest() throws JSONException, IOException {
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<String> listCodes = RealCodeUtil.getCodesStr(400,Arrays.asList(codeArray));
        for (int i = 0; i < listCodes.size(); i++) {
            ResponseEntity<String> entity = restTemplate.getForEntity("http://qt.gtimg.cn/q="+listCodes.get(i),String.class);
            System.out.println(entity.getBody());
        }
    }

    @Test
    public void httpDownload(){
        String [] codeArray = Constants.STOCK_CODE.split(",");
        List<List<String>> listCodes = RealCodeUtil.getCodesList(1000,Arrays.asList(codeArray));
        latch = new CountDownLatch(listCodes.size());
        for (int i = 0; i < listCodes.size(); i++) {
            new Thread(new DownLoadThread(listCodes.get(i),restTemplate,latch)).start();
        }
        /**
         * 遇到的问题：由于主线程结束，导致创建的线程未正常执行完成就被动结束
         */
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



    class DownLoadThread implements Runnable{
        private List<String> codes;
        private RestTemplate restTemplate;
        private CountDownLatch latch;
        public DownLoadThread(List<String> codes,RestTemplate restTemplate,CountDownLatch latch){
            this.codes=codes;
            this.restTemplate=restTemplate;
            this.latch=latch;
        }
        @Override
        public void run() {
            for (String code:codes) {
                code=code.replace("sh","0");
                code=code.replace("sz","1");
                String url = "http://quotes.money.163.com/cjmx/2019/20191030/"+code+".xls";
                HttpTest.downloadHttp(restTemplate,url,code,0);
            }
            latch.countDown();
        }
    }

    public static void downloadHttp(RestTemplate restTemplate,String url,String code,int next){
        ResponseEntity<byte[]> response = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            HttpEntity<Resource> httpEntity = new HttpEntity<Resource>(headers);
            response = restTemplate.exchange(url, HttpMethod.GET,
                    httpEntity, byte[].class);
            File file = new File("E:\\excel\\stock1\\"+code+".xls");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(response.getBody());
            fos.flush();
            fos.close();
        } catch (HttpClientErrorException e) {
            if(next < 5){
                downloadHttp(restTemplate,url,code,++next);
            }else {
                System.out.println(url);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void download(){
        HttpTest.downloadHttp(restTemplate,"http://quotes.money.163.com/cjmx/2019/20191030/0601988.xls","601988",0);
    }

    @Test
    public void loadFile(){
        List<String> fileNames = getAllFileName("E:\\excel\\stock\\");
        for(String name:fileNames){
            excutor.execute(new ReadThread("E:\\excel\\stock\\",name,restTemplate));
            long count = excutor.getTaskCount()-excutor.getCompletedTaskCount();
            System.out.println("RealPriceTask:总数:"+excutor.getTaskCount()+"完成:"+excutor.getCompletedTaskCount()+"等待:"+count+"线程数量:"+excutor.getPoolSize());

        }
    }

    class ReadThread implements Runnable{
        private String filePath;
        private String fileName;
        private RestTemplate restTemplate;
        public ReadThread(String filePath,String fileName,RestTemplate restTemplate){
            this.filePath = filePath;
            this.fileName = fileName;
            this.restTemplate = restTemplate;
        }
        @Override
        public void run() {
            HttpTest.readExcel(filePath,fileName,restTemplate);
        }
    }

    public static void readExcel(String filePath,String fileName,RestTemplate restTemplate){
        String fullPath = filePath+fileName;
        ImportParams params = new ImportParams();
        params.setTitleRows(0);
        params.setHeadRows(1);
        List<ClinchDetail> list = ExcelImportUtil.importExcel(
                new File(fullPath),
                ClinchDetail.class, params);
        String code = fileName.substring(0,8);
        code=code.replaceFirst("0","sh");
        code=code.replaceFirst("1","sz");
        ResponseEntity<String> entity = restTemplate.getForEntity("http://qt.gtimg.cn/q="+code,String.class);
        CapitalInfo capitalInfo =resultSplit(entity.getBody());
        //资金大小
        AtomicReference<Double> capitalSize = new AtomicReference<>((double) 0);
        AtomicInteger inBigBill = new AtomicInteger();
        AtomicInteger outBigBill = new AtomicInteger();
        list.stream().forEach(o ->{
            boolean isBig =o.getClinchSum() > 300000;
            if(o.getClinchNature() == 0){
                capitalSize.set(capitalSize.get() - o.getClinchPrice());
                if (isBig)
                    outBigBill.getAndIncrement();
            }else if(o.getClinchNature() == 1){
                capitalSize.set(capitalSize.get() + o.getClinchPrice());
                if (isBig)
                    inBigBill.getAndIncrement();
            }
        });
        int capitalFlow = capitalSize.get() < 0 ? 0:1;
        capitalInfo.setCapitalFlow(capitalFlow);
        capitalInfo.setCapitalSize(capitalSize.get());
        capitalInfo.setInBigBill(inBigBill.get());
        capitalInfo.setOutBigBill(outBigBill.get());
        System.out.println(JSON.toJSONString(capitalInfo));
    }

    public static CapitalInfo resultSplit(String result){
        if (StringUtils.isEmpty(result.trim()))
            return null;
        String[] values = result.split("~");
        if(values.length < 38)
            return null;
        return transform(values);
    }

    public static CapitalInfo transform(String[] values){
        Date date = new Date();
        String time = String.format("%tF%n",date);
        CapitalInfo capitalInfo = CapitalInfo.builder()
                .stockName(values[1])
                .dealTime(time)
                .rose(Double.valueOf(values[32]))
                .exchange(values[38])
                .build();
        return capitalInfo;
    }

    /**
     * 获取文件夹下的所有文件名
     * @param path
     */
    public static List<String> getAllFileName(String path) {
        List<String> list = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                list.add(tempList[i].getName());
            }
        }
        return list;
    }
    public static void main(String[] args) {
        String code = "sz002779";
        code = code.substring(2,code.length());
        System.out.println(code);
    }
}
