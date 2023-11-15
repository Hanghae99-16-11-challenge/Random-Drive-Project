function calculateDistance(lat1 , lon1, lat2, lon2)
{
    lat1 = lat1 * (Math.PI / 180);
    lon1 = lon1 * (Math.PI / 180);
    lat2 = lat2 * (Math.PI / 180);
    lon2 = lon2 * (Math.PI / 180);
    let earthRadius = 6371;
    return earthRadius * Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));
}

//kakao.maps.services.Geocoder() 안됨

function calculateCurrectToPoint(data)
{
    // y -> lat ,x -> lon
    //data.routes[0].summary.origin.y, data.routes[0].summary.origin.x
    //반경 기반 랜덤 길찾기 할때 현위치랑 출발기 거리 측정

    // var originAddress = document.getElementById('all-random-originAddress').value;//반경 기반 랜덤 길찾기 - 출발지

    if (data == null)
    {
        console.warn("길이 없음");
        return;
    }

    var let_ori = data.routes[0].summary.origin.y;
    var lon_ori = data.routes[0].summary.origin.x;
    var lat_des = data.routes[0].summary.destination.y;
    var lon_des = data.routes[0].summary.destination.x;

    navigator.geolocation.getCurrentPosition(function(position) {
        // 위치 정보를 표시하기
        // $("#location2").text("\n, GPS 위치 정보: " + position.coords.latitude + ", " + position.coords.longitude);
        var C_lat = position.coords.latitude;
        var C_lon = position.coords.longitude;

        //response.getRoutes()[0].getSummary().getDuration()
        let spend = data.routes[0].summary.duration;

        console.warn("=====현재위치와 출발지간의 직선거리 : " + calculateDistance(C_lat, C_lon, let_ori, lon_ori) + "km" +
            "\n현재위치와 도착지 간의 직선거리 : " + calculateDistance(C_lat, C_lon, lat_des, lon_des) + "km" +
            "\n경로 길이 : " + data.routes[0].summary.distance +"m , 소요 시간 : " +
            Math.floor(spend / 60) + "분" + (spend - (Math.floor(spend / 60) * 60)) + "초");

        //"m / 소요 시간 : " + duration + " -> " + (duration / 60) + "m" + (duration - ((duration/60) * 60)) + "s"

    });
}


function EditMark(mark , infoWin , lat, log, text)
{
    if (mark === null)
    {
        mark = new kakao.maps.Marker();
    }
    if (infoWin === null)
    {
        infoWin = new kakao.maps.InfoWindow();
    }

    // mark.position = new kakao.maps.LatLng(lat, log);
    mark.setPosition(new kakao.maps.LatLng(lat, log));

    infoWin.setPosition(new kakao.maps.LatLng(lat, log));
    infoWin.setContent(text);

    mark.setMap(map);
    infoWin.open(map, mark);
}

//navigation.html 지도 클릭시 작동
kakao.maps.event.addListener(map, 'click', updateMap);

function updateMap(mouseEvent)
{
    // 클릭한 위도, 경도 정보를 가져옵니다
    var latlng = mouseEvent.latLng;

    // var message = '클릭한 위치의 위도는 ' + latlng.La + ' 이고, ';
    // message += '경도는 ' + latlng.Ma + ' 입니다';
    // console.log(message);

    update_refact(latlng.Ma, latlng.La);
}

function update_refact(lat, lng)
{
    EditMark(positionMark, positionText, lat, lng, '현재 위치');

    if(routeData == null)
    {
        console.log("길 찾기 정보 없음");
    }else
    {
        if (routeData.guides.length === 0)
            return;

        let closeData = {
            data: null,
            squaredLength: -1
        }
        panTo(lat, lng);//화면 이동

        let progress = 0;
        //routeData.roads
        for (let sec = 0; sec < routeData.roads.length; sec++)// for (let sec = 0; sec < pathData.routes[0].sections.length; sec++)
        {
            for (var guid = 0; guid < routeData.roads[sec].length; guid++, progress++)
            {
                // var guidData = pathData.routes[0].sections[sec].guides[guid];
                let guidData = routeData.guides[progress];

                if (closeData.squaredLength < 0)
                {
                    closeData.data = guidData;
                    closeData.squaredLength = calculateDistance(lat, lng, guidData.y, guidData.x);
                }else
                {
                    if (guidData === undefined)
                        break;

                    var tempPoint = calculateDistance(lat, lng, guidData.y, guidData.x);

                    if (closeData.squaredLength > tempPoint)
                    {
                        closeData.data = guidData;
                        closeData.squaredLength = tempPoint;
                    }
                    // 이전 값과 거리비교
                }
            }
        }// 클릭한 지점에서 가장 가까운 안내 지점

        let point = routeData.guides[naviInfo_ProcessIndex];

        console.log("refacted 다음 안내지점 => " + point.y + " , " + point.x + " => " + calculateDistance(lat, lng, point.y, point.x));

        //20m 이내로 접근한다면 다음 안내
        if (calculateDistance(lat, lng, point.y, point.x) < (offetUserRadius * 0.001))
        {
            naviInfo_ProcessIndex++;

            updateMark();//마크 표시 , getGuidPoint 과 같음

            // aunceGuid();//TTS 안내
            speakText(point.guidance + " 입니다.");
            Anuce_State = 2; // 교차로 진입 안내
        }

        switch (naviInfo_State)
        {
            case 0:
            {
                stopNavi();
                // reset();
                onClick_StopNavi_navi();

                console.log("길 안내 종료");
                speakText("길 안내를 종료합니다.");//TTS
                return;
            }
            case -1:
            {
                stopNavi();
                onClick_StopNavi_navi();
                console.warn("길 안내 오류");
                return;
            }
            // default:
            //     speakText(routeData.guides[naviInfo_ProcessIndex].guidance + " 입니다.");
        }

        {
            clearPolylines();

            let linePath_Calculate = [];//leftDistance_road(lat, lng);
            let currectPathLine = [];

            {
                pathLeftDistance = 0;
                pathLeftDuration = 0;
                nextGuidDistacne = 0;
                nextGuidDuration = 0;
            }//남은 시간, 거리 리셋

            {
                //roads 크기와 Guids 크기 같음 , 대신 Guids에는 출발지,도착지 구분하는 항목이 들어가있음 (그래서 길이,소요시간 값 없음)
                

                {
                    for (let progress = naviInfo_ProcessIndex; progress < routeData.guides.length - 1; progress++)
                    {
                        if (progress >= routeData.roads.length)
                            break;

                        for(let i = 0; i < routeData.roads[progress].vertexes.length; i += 2)
                        {
                            linePath_Calculate.push(new kakao.maps.LatLng(routeData.roads[progress].vertexes[i + 1], routeData.roads[progress].vertexes[i]));
                        }
                    }//지나가지 않은 경로 ,  Guids에는 출발지,도착지 구분하는 항목이 들어가있어 길이에 2를 뺌
                }//지나가지 않은 경로


                {
                    let currectRoads = [];
                    let lastRoadIndex = -1;

                    if (routeData.roads.length <= (naviInfo_ProcessIndex - 1))
                    {
                        console.warn("뭔가 문제 발생 - calculation.js 지나가고 있는 도로 그리는데 오류 발생");
                        currectRoads = routeData.roads[routeData.roads.length - 1];
                    }else
                    {
                        currectRoads = routeData.roads[naviInfo_ProcessIndex - 1];
                    }


                    currectPathLine.push(new kakao.maps.LatLng(lat, lng));

                    for(let i = 0; i < currectRoads.vertexes.length; i += 2)
                    {
                        if (calculateDistance(currectRoads.vertexes[1], currectRoads.vertexes[0], currectRoads.vertexes[i + 1], currectRoads.vertexes[i])
                            > calculateDistance(currectRoads.vertexes[1], currectRoads.vertexes[0], lat, lng))
                        {
                            currectPathLine.push(new kakao.maps.LatLng(currectRoads.vertexes[i + 1], currectRoads.vertexes[i]));
                        }else
                        {
                            lastRoadIndex = i;
                        }
                    }


                    if (lastRoadIndex > 0)
                    {
                        let vex = currectRoads.vertexes;

                        let roadPartLength = calculateDistance(vex[lastRoadIndex + 3], vex[lastRoadIndex + 2],vex[lastRoadIndex + 1], vex[lastRoadIndex]);
                        let PastToPos = calculateDistance(vex[lastRoadIndex + 1], vex[lastRoadIndex], lat, lng);
                        let CurrectToPos = 0;
                        CurrectToPos = calculateDistance(vex[lastRoadIndex + 3], vex[lastRoadIndex + 2], lat, lng);

                        if (isNaN(CurrectToPos))
                        {
                            CurrectToPos = calculateDistance(vex[lastRoadIndex - 1], vex[lastRoadIndex - 2], lat, lng);
                            roadPartLength = calculateDistance(vex[lastRoadIndex - 1], vex[lastRoadIndex - 2],vex[lastRoadIndex + 1], vex[lastRoadIndex]);
                        }
                        let leftroad = (((PastToPos + CurrectToPos) - roadPartLength) * 1000);
                        if ((leftroad > offetUserRadius * 2))
                        {
                            outOfPath(lat, lng);
                            return;
                        }
                    }//비정확 하지만
                    else if (lastRoadIndex === 0 && offCourseCount > 0)
                    {
                        let vex = currectRoads.vertexes;

                        let roadPartLength = calculateDistance(vex[lastRoadIndex + 3], vex[lastRoadIndex + 2],vex[lastRoadIndex + 1], vex[lastRoadIndex]);
                        let PastToPos = calculateDistance(vex[lastRoadIndex + 1], vex[lastRoadIndex], lat, lng);
                        let CurrectToPos = 0;
                        CurrectToPos = calculateDistance(vex[lastRoadIndex + 3], vex[lastRoadIndex + 2], lat, lng);

                        if (isNaN(CurrectToPos))
                        {
                            CurrectToPos = calculateDistance(vex[lastRoadIndex - 1], vex[lastRoadIndex - 2], lat, lng);
                            roadPartLength = calculateDistance(vex[lastRoadIndex - 1], vex[lastRoadIndex - 2],vex[lastRoadIndex + 1], vex[lastRoadIndex]);
                        }
                        let leftroad = (((PastToPos + CurrectToPos) - roadPartLength) * 1000);
                        if ((leftroad > offetUserRadius * 2))
                        {
                            outOfPath(lat, lng);
                            return;
                        }
                    }

                }//지나가고있는 도로의 Vertex 저장 + 경로 이탈 감지

                for (let progress = naviInfo_ProcessIndex; progress < routeData.guides.length; progress++)
                {
                    pathLeftDistance += routeData.guides[progress].distance;
                    pathLeftDuration += routeData.guides[progress].duration;
                }//안내 정보 + 남은 거리 , 남은 시간 계산


                // routeData.guides[naviInfo_ProcessIndex - 1].distance / 현재 도로
                nextGuidDistacne =
                    calculateDistance(routeData.guides[naviInfo_ProcessIndex].y, routeData.guides[naviInfo_ProcessIndex].x, lat, lng) * 1000;
                nextGuidDuration = (nextGuidDistacne / routeData.guides[naviInfo_ProcessIndex].distance)
                                    * routeData.guides[naviInfo_ProcessIndex].duration;

                pathLeftDistance += nextGuidDistacne;
                pathLeftDuration += nextGuidDuration;
            }//경로 , 남은 시간 , 남은 거리 계산 + 경로 이탈 계산

            Update_GuidIndo_navi();

            let polyline = new kakao.maps.Polyline({
                path: currectPathLine.concat(linePath_Calculate),//linePath_Calculate
                strokeWeight: 5,
                strokeColor: '#007bff',
                strokeOpacity: 0.7,
                strokeStyle: 'solid'
            });

            polyline.setMap(map);

            polylines.push(polyline); // 선을 배열에 추가
        }//경로 그리기 + 경로 계산

        console.log("다음 안내까지 남은 비율 : " + (nextGuidDistacne / routeData.guides[naviInfo_ProcessIndex].distance).toFixed(1) * 100 + "%");
        //======
        // 도로 길이가 일정 크기(200M) 이상일때 100m 전에도 안내 (잠시후 어느 방향)
        //  일정 크기 미만 일때는 50M 전에 안내 (어느 방향으로 )

        // 다음 안내 지점 안내 하면 --> 다음 안내지점 대해 어느방향 가야할지 알려주고 , 다음에 얼마나 가야할지 알려줌
        // ===> 100m 들어올때 : 잠시후 어느방향으로 우회전 입니다.
        // ===> 30m 들어올때 : 어느방향으로 우회전 입니다.
        // ===> 30m 나갈때 : 다음 안내까지 몇m , 약 몇초 소요 됩니다.

        // ===> AnuceState = {경로 시작 , 미리 안내, 교차로 진입전 , 교차로 벗어남 , 경로 종료}

        {
            if (nextGuidDistacne <= 100 && Anuce_State !== -1 && routeData.guides[naviInfo_ProcessIndex].distance > 200)
            {
                speakText("잠시후 " + routeData.guides[naviInfo_ProcessIndex].guidance + " 입니다.");
                Anuce_State = -1;
            }//사전 안내 , 도로가 200m 보다 길때 한번만

            if (calculateDistance(lat, lng, point.y, point.x) >= (offetUserRadius * 0.001) && Anuce_State === 2)
            {
                let anceKM = Math.floor(routeData.guides[naviInfo_ProcessIndex].distance / 1000);
                let anceM = routeData.guides[naviInfo_ProcessIndex].distance - (anceKM * 1000);
                let anceKMDemical = (routeData.guides[naviInfo_ProcessIndex].distance / 1000).toFixed(1);// 1.5 이면 5 라고 읽음

                if (anceKM > 0)
                {
                    speakText(anceKMDemical + "km 후 다음 안내지점 입니다.");
                }else
                {
                    speakText(Math.round(nextGuidDistacne) + "m 후 다음 안내지점 입니다.");
                }
                Anuce_State = 3; // 교차로 진출 안내
            }
        }
    }
}

//다음에 올 안내지점 반환, naviInfo_State, 마크 설정
function updateMark()
{

    if (routeData.guides.length <= naviInfo_ProcessIndex)
    {
      naviInfo_State = 0;
      return;
    }//안내 종료
    if (naviInfo_ProcessIndex < 0)
    {
        naviInfo_State = -1;
        return;
    }//안내 정보 오류

    let point = routeData.guides[naviInfo_ProcessIndex];

    if (point.type === 1000 && point.road_index === 0)
    {
        naviInfo_ProcessIndex++;
        point = routeData.guides[naviInfo_ProcessIndex];
    }// 경유지가 2개인경우 , 도로의 시작점인 경유지를 스킵

    switch (point.type)
    {
        // case 100:
        // {
        //     naviInfo_State = 4;
        //     break;
        // }//출발지
        case 101:
        {
            naviInfo_State = 3;
            break;
        }//목적지
        case 1000:
        {
            naviInfo_State = 2;
            break;
        }//경유지
        default:
        {
            naviInfo_State = 1;
        }
    }

    let stateText = '';
    switch (naviInfo_State)
    {
        case -1:
            stateText = '경로가 없음';
            break;
        case 0:
            stateText = '도착 하였습니다';
            break;
        case  1:
            stateText = '다음 안내 지점';
            break;
        case  2:
            stateText = '경유지 도착';
            console.log("waypointCount +1" + waypointCount)
            waypointCount += 1;
            break;
        case 3:
            stateText = '곧 목적지 입니다';
            break;
        default:
            stateText = '';
    }

    EditMark(naviInfoMark, naviInfoText, point.y, point.x, stateText);
}



function startNavi()
{
    // getNextGuidPoint(false);
    // getGuidPoint(true);
    updateMark();
    startCorutine();
    update_GuidInfo();

    speakText("안내를 시작합니다.");
    aunceGuid(false);
}
function stopNavi()
{
    naviInfo_SectionIndex = 0;
    naviInfo_GuidIndex = 1;
    naviInfo_ProcessIndex = 1;
    naviInfo_State = -1;
    offCourseCount = 0;
    Anuce_State = 0;


    positionMark.setMap(null);
    positionText.close();
    naviInfoMark.setMap(null);
    naviInfoText.close();

    routeData = null;
    stopCoroutine();
    clearPolylines();
    stopText();
}

//네비게이션 안내 초기화
function clearNavi()
{
    naviInfo_SectionIndex = 0;
    naviInfo_GuidIndex = 1;
    naviInfo_ProcessIndex = 1;
    Anuce_State = 0;

    positionMark.setMap(null);
    positionText.close();
    naviInfoMark.setMap(null);
    naviInfoText.close();


    if (routeData != null && routeData != undefined)
    {
        updateMark();

        let startPoint = routeData.guides[0];
        update(startPoint.y, startPoint.x);

        map.setLevel(3, {animate: true});// 사용시 보이는 위치 달라짐
        panTo(startPoint.y, startPoint.x);
    }    //========================================= 길 재생성후 이부분 실행 필요


    // startCorutine();
}



function panTo(lat , lng) {
    // 이동할 위도 경도 위치를 생성합니다
    var moveLatLon = new kakao.maps.LatLng(lat, lng);

    // 지도 중심을 부드럽게 이동시킵니다
    // 만약 이동할 거리가 지도 화면보다 크면 부드러운 효과 없이 이동합니다
    map.panTo(moveLatLon);
}
function panToStart()
{
    let startPoint = routeData.guides[0];

    map.setLevel(3, {animate: true});// 사용시 보이는 위치 달라짐
    panTo(startPoint.y, startPoint.x);
}
function outOfPath(lat, lng)
{
    if (researchDelay_intervalId < 0)
    {
        researchDelay_intervalId = setInterval(function () {
            speakText("경로를 이탈하여 재생성합니다.");
            if (pathType() === 'live')
            {
                remakeNavi(lat, lng);//응답시 resetNavi() 실행호출 준비
            }
            else
            {
                remakeRandomNavi(lat, lng);
            }
            console.warn("경로 이탈");

            clearInterval(researchDelay_intervalId);

            researchDelay_intervalId = -1;

        }, 1000);
    }
}

function aunceGuid(cencleProcess = true)
{
    if (naviInfo_State >= 1)
    {

        let anceKM = Math.round(routeData.guides[naviInfo_ProcessIndex].distance / 1000);
        let anceKMDemical = (routeData.guides[naviInfo_ProcessIndex].distance / 1000).toFixed(1);// 1.5 이면 5 라고 읽음

        let anceM = routeData.guides[naviInfo_ProcessIndex].distance % 1000;
        if (anceKM >= 1)
        {
            speakText("다음 안내까지 " + anceKMDemical + "km 후\n"
                + routeData.guides[naviInfo_ProcessIndex].guidance + " 입니다.", cencleProcess);
        }else
        {
            speakText("다음 안내까지 " + anceM + "m 후\n"
                + routeData.guides[naviInfo_ProcessIndex].guidance + " 입니다.", cencleProcess);
        }

    }//TTS 안내 / 시간으로 했을때는 너무 유동적
}

//=======================
//  미사용 이지만 아직 의존성 있음

// 이전 코드 , 만약 쓸 경우 update_refact() 으로 넘겨줌
function update(lat, lng)
{
    //navigation.html 지도 클릭시 작동
    //\n 클릭한 위도, 경도 정보를 가져옵니다

    console.warn("Replace update_refact");
    update_refact(lat, lng);
    return;

    EditMark(positionMark, positionText, lat, lng, '현재 위치');

    // calculateCurrectToPoint(pathData);//이건 잘됨

    if(pathData == null)
    {
        console.log("길 찾기 정보 없음");
    }else
    {

        // pathData.routes[0].sections[0].guides
        //routes -> 경로 , sections -> 구간(경유지마다 추가)

        var closeData = {
            data: pathData.routes[0].sections[0].guides[0],
            squaredLength: -1
        }
        panTo(lat, lng);//화면 이동

        for (var sec = 0; sec < pathData.routes[0].sections.length; sec++)
        {
            for (var guid = 0; guid < pathData.routes[0].sections[sec].guides.length; guid++)
            {
                var guidData = pathData.routes[0].sections[sec].guides[guid];

                if (closeData.squaredLength < 0)
                {
                    closeData.data = guidData;
                    closeData.squaredLength = calculateDistance(lat, lng, guidData.y, guidData.x);
                }else
                {
                    var tempPoint = calculateDistance(lat, lng, guidData.y, guidData.x);

                    if (closeData.squaredLength > tempPoint)
                    {
                        closeData.data = guidData;
                        closeData.squaredLength = tempPoint;
                    }
                    // 이전 값과 거리비교
                }
            }
        }// 클릭한 지점에서 가장 가까운 안내 지점


        var point = getGuidPoint(true);

        console.log("다음 안내지점 => " + point.y + " , " + point.x + " => " + calculateDistance(lat, lng, point.y, point.x));

        //20m 이내로 접근한다면 다음 안내
        if (calculateDistance(lat, lng, point.y, point.x) < (20 * 0.001))
        {
            getNextGuidPoint(true);
            getGuidPoint(true);
        }

        if (naviInfo_State === 0)
        {
            stopNavi();
            // reset();
            onClick_StopNavi_navi();
            console.log("길 안내 종료");

            return;
        }

        // pathData.routes[0].sections[0].roads[0].vertexes[1] -> lat (길 기준)

        var linePath_Calculate = leftDistance_road(lat, lng);
        var currectPathLine = [];

        // update_GuidInfo();//======길찾기 정보 업데이트
        Update_GuidIndo_navi();

        {
            //현재 도로 의 vertexes 를 완벽하게 측정x 이므로
            // 도로 시작점 과 도로vertex 거리 , 도로 시작점과 현제지점 의 거리 비교해
            // 도로 시작점과 현제지점 의 거리 보다 가장 먼저 큰 길이 나온 지점을 안내

            var currectPos = pathData.routes[0].sections[naviInfo_SectionIndex].roads[Math.max(0, naviInfo_GuidIndex - 1)];

            currectPathLine.push(new kakao.maps.LatLng(lat, lng));

            if (currectPos != null)
            {
                for(let i = 0; i < currectPos.vertexes.length; i += 2)
                {
                    if (calculateDistance(currectPos.vertexes[1], currectPos.vertexes[0], currectPos.vertexes[i + 1], currectPos.vertexes[i])
                        > calculateDistance(currectPos.vertexes[1], currectPos.vertexes[0], lat, lng))
                    {
                        currectPathLine.push(new kakao.maps.LatLng(currectPos.vertexes[i + 1], currectPos.vertexes[i]));
                    }
                }
            }

            let polyline = new kakao.maps.Polyline({
                path: currectPathLine.concat(linePath_Calculate),
                strokeWeight: 5,
                strokeColor: '#007bff',
                strokeOpacity: 0.7,
                strokeStyle: 'solid'
            });

            polyline.setMap(map);

            polylines.push(polyline); // 선을 배열에 추가
        }

    }
}
//다음에 올 안내 정보를 찾음 (Section, Guid 인덱스 을 저장)
// 반환이 0 일때 이미 도착지점일때 , -1 : 유효하지 않은 경로일때 , 1 : 성공 , 2 : 구간 시작점, 3 : 다음이 도착지점일때
function getNextGuidPoint(add = true)
{
    // getGuidPoint(true);

    {
        try {
            pathData.routes[0].sections[0].guides[1];
        }catch (e)
        {
            console.log(e.message);

            naviInfo_State = -1;
            return -1;
        }
    }//유효성 검사

    var LastSectionIdex = pathData.routes[0].sections.length - 1;


    if (naviInfo_SectionIndex >= LastSectionIdex)
    {
        if ((naviInfo_GuidIndex + 1) === (pathData.routes[0].sections[LastSectionIdex].guides.length - 1))
        {
            if (add)
            {
                // naviInfo_SectionIndex = naviInfo_SectionIndex + 1;/xxx
                naviInfo_GuidIndex = naviInfo_GuidIndex + 1;
                naviInfo_ProcessIndex++;
            }

            // console.log("곧 목적지 입니다. => " + naviInfo_SectionIndex + " : " + naviInfo_GuidIndex);
            naviInfo_State = 3;
            return 3;
        }
        else if (((naviInfo_GuidIndex + 1) > (pathData.routes[0].sections[LastSectionIdex].guides.length - 1)))
        {
            // console.log("이미 목적지 입니다");
            // naviInfo_SectionIndex = naviInfo_SectionIndex + 1;
            // naviInfo_GuidIndex = naviInfo_GuidIndex + 1;

            naviInfo_State = 0;
            return 0;
        }
    }

    if ((naviInfo_GuidIndex + 1) >= pathData.routes[0].sections[naviInfo_SectionIndex].guides.length)
    {
        if (add)
        {
            naviInfo_SectionIndex = naviInfo_SectionIndex + 1;
            naviInfo_GuidIndex = 1;
            naviInfo_ProcessIndex++;
        }

        // console.log("경유지 도착 => " + naviInfo_SectionIndex + " : " + naviInfo_GuidIndex);
        naviInfo_State = 2;
        return 2;
    }else
    {
        if (add)
        {
            naviInfo_GuidIndex = naviInfo_GuidIndex + 1;
            naviInfo_ProcessIndex++;
        }

        // console.log("안내 지점 =>" + naviInfo_SectionIndex + " : " + naviInfo_GuidIndex);
        naviInfo_State = 1;
        return 1;
    }
}
function getGuidPoint(marked = true)
{
    var temp_sec = pathData.routes[0].sections[Math.min(naviInfo_SectionIndex, (pathData.routes[0].sections.length - 1))];
    var temp_gui = temp_sec.guides[Math.min(naviInfo_GuidIndex, (temp_sec.guides.length - 1))];

    if (marked)
    {
        var stateText = '';
        switch (naviInfo_State)
        {
            case -1:
                stateText = '경로가 없음';
                break;
            case 0:
                stateText = '도착 하였습니다';
                break;
            case  1:
                stateText = '다음 안내 지점';
                break;
            case  2:
                stateText = '경유지 도착';
                break;
            case 3:
                stateText = '곧 목적지 입니다';
                break;
            default:
                stateText = '';
        }

        EditMark(naviInfoMark, naviInfoText, temp_gui.y, temp_gui.x, stateText);
    }

    return temp_gui;
}