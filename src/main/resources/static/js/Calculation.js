
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

        console.log("현제위치와 출발지간의 직선거리 : " + calculateDistance(C_lat, C_lon, let_ori, lon_ori) + "km" +
            "\n현제위치와 도착지 간의 직선거리 : " + calculateDistance(C_lat, C_lon, lat_des, lon_des) + "km" +
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

    update(latlng.Ma, latlng.La);
}

function update(lat, lng)
{
    //navigation.html 지도 클릭시 작동
    //\n 클릭한 위도, 경도 정보를 가져옵니다

    EditMark(positionMark, positionText, lat, lng, '클릭한 위치');

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
            reset();
            return;
        }

        // pathData.routes[0].sections[0].roads[0].vertexes[1] -> lat (길 기준)

        var linePath_Calculate = leftDistance_road(lat, lng);
        var currectPathLine = [];

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

function leftDistance(lat, lng)
{

    var leftDis = 0;
    var linePath = [];
    clearPolylines();

    for (var sec = naviInfo_SectionIndex; sec < pathData.routes[0].sections.length; sec++)
    {
        console.log("> Find : " + sec + " / " + guid + " // " + "Now : " + naviInfo_SectionIndex + " / " + naviInfo_GuidIndex
            +" / Length : " + pathData.routes[0].sections.length + " / " + pathData.routes[0].sections[sec].guides.length);

        for (var guid = 1; guid < pathData.routes[0].sections[sec].guides.length; guid++)
        {
            if (sec === naviInfo_SectionIndex && guid < naviInfo_GuidIndex)
            {
                continue;
            }
            else if (sec === naviInfo_SectionIndex && guid === naviInfo_GuidIndex)
            {
                var Ldata = pathData.routes[0].sections[naviInfo_SectionIndex].guides[naviInfo_GuidIndex];

                leftDis += (calculateDistance(lat, lng, Ldata.y, Ldata.x) * 1000);//현제 경로의 남은 경로

                linePath.push(new kakao.maps.LatLng(Ldata.y, Ldata.x));
                continue;
            }

            var Ldata = pathData.routes[0].sections[sec].guides[guid];

            leftDis += Ldata.distance;

            linePath.push(new kakao.maps.LatLng(Ldata.y, Ldata.x));
        }
    }


    let polyline = new kakao.maps.Polyline({
        path: linePath,
        strokeWeight: 5,
        strokeColor: '#007bff',
        strokeOpacity: 0.7,
        strokeStyle: 'solid'
    });

    polyline.setMap(map);

    polylines.push(polyline); // 선을 배열에 추가
}
function leftDistance_road(lat, lng)
{

    var leftDis = 0;
    var leftDur = 0;

    var linePath = [];
    clearPolylines();

    for (var sec = naviInfo_SectionIndex; sec < pathData.routes[0].sections.length; sec++)
    {
        // console.log("> Find : " + sec + " / " + guid + " // " + "Now : " + naviInfo_SectionIndex + " / " + naviInfo_GuidIndex
        //     +" / Length : " + pathData.routes[0].sections.length + " / " + pathData.routes[0].sections[sec].roads.length);

        for (var guid = 1; guid < pathData.routes[0].sections[sec].roads.length; guid++)
        {
            var currectPos;

            if (sec === naviInfo_SectionIndex && guid < naviInfo_GuidIndex)
            {
                continue;
            }
            else if (sec === naviInfo_SectionIndex && guid === naviInfo_GuidIndex)
            {
                currectPos = pathData.routes[0].sections[naviInfo_SectionIndex].roads[naviInfo_GuidIndex];

                leftDis += pathData.routes[0].sections[sec].roads[guid].distance;
                leftDur += pathData.routes[0].sections[sec].roads[guid].duration;
            }else
            {
                currectPos = pathData.routes[0].sections[sec].roads[guid];

                leftDis += pathData.routes[0].sections[sec].roads[guid].distance;
                leftDur += pathData.routes[0].sections[sec].roads[guid].duration;
            }

            for(let i = 0; i < currectPos.vertexes.length; i += 2)
            {
                linePath.push(new kakao.maps.LatLng(currectPos.vertexes[i + 1], currectPos.vertexes[i]));
            }

            // 그러니  routes -> 경로 / section -> 다음 경유지까지의 구간 / guid -> 다음 안내 / road -> 도로 / road.vertexes -> (지도 그리기용) 직선 거리
        }
    }

    var point = getGuidPoint(false);
    var leftNextPointDis = (calculateDistance(lat, lng, point.y, point.x) * 1000);
    pathLeftDistance = leftDis + leftNextPointDis;

    //이전 지점과 다음지점의 직전거리를 계산해 , 다음 안내지점까지 남은거리 와의 비율으로 도착까지 남은 시간 예측
    {
        var currectPath = pathData.routes[0].sections[naviInfo_SectionIndex].roads[naviInfo_GuidIndex - 1];
        var nextPath = pathData.routes[0].sections[naviInfo_SectionIndex].roads[naviInfo_GuidIndex];

        if (nextPath != null)
        {
            var nextLineDis = calculateDistance(currectPath.vertexes[1], currectPath.vertexes[0], nextPath.vertexes[1], nextPath.vertexes[0]) * 1000;

            pathLeftDuration = leftDur + (currectPath.duration * (leftNextPointDis/nextLineDis));
        }
    }
    console.log("==> 목적지 도착까지 남은 거리 : " + pathLeftDistance.toFixed(2) + "m / 남은 시간 : " + pathLeftDuration.toFixed(2) + "s");

    let polyline = new kakao.maps.Polyline({
        path: linePath,
        strokeWeight: 5,
        strokeColor: '#007bff',
        strokeOpacity: 0.7,
        strokeStyle: 'solid'
    });

    polyline.setMap(map);

    // polylines.push(polyline); // 선을 배열에 추가//=========== 갑자기 안됌
    polylines = [polyline];

    // 그러니  routes -> 경로 / section -> 다음 경유지까지의 구간 / guid -> 다음 안내 / road -> 도로 / road.vertexes -> (지도 그리기용) 직선 거리
    //  (naviInfo_SectionIndex, naviInfo_GuidIndex) 은 다음 안내 지점부터의 길
    return linePath;
}

function startNavi()
{
    getNextGuidPoint(false);
    getGuidPoint();
    startCorutine();
}
function stopNavi()
{
    naviInfo_SectionIndex = 0;
    naviInfo_GuidIndex = 1;
    naviInfo_State = -1;

    positionMark.setMap(null);
    positionText.close();
    naviInfoMark.setMap(null);
    naviInfoText.close();

    clearPolylines();
}

//네비게이션 안내 초기화
function clearNavi()
{
    naviInfo_SectionIndex = 0;
    naviInfo_GuidIndex = 1;

    if (pathData != null)
        naviInfo_State = getNextGuidPoint(false);

    positionMark.setMap(null);
    positionText.close();
    naviInfoMark.setMap(null);
    naviInfoText.close();

    if (pathData != null)
        drawPolylines(pathData);

    startCorutine();
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
//다음에 올 안내 정보를 찾음 (Section, Guid 인덱스 을 저장)
// 반환이 0 일때 이미 도착지점일때 , -1 : 유효하지 않은 경로일때 , 1 : 성공 , 2 : 구간 시작점, 3 : 다음이 도착지점일때
function getNextGuidPoint(add = true)
{
    getGuidPoint(true);

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
        }

        // console.log("경유지 도착 => " + naviInfo_SectionIndex + " : " + naviInfo_GuidIndex);
        naviInfo_State = 2;
        return 2;
    }else
    {
        if (add)
        {
            naviInfo_GuidIndex = naviInfo_GuidIndex + 1;
        }

        // console.log("안내 지점 =>" + naviInfo_SectionIndex + " : " + naviInfo_GuidIndex);
        naviInfo_State = 1;
        return 1;
    }
}

function panTo(lat , lng) {
    // 이동할 위도 경도 위치를 생성합니다
    var moveLatLon = new kakao.maps.LatLng(lat, lng);

    // 지도 중심을 부드럽게 이동시킵니다
    // 만약 이동할 거리가 지도 화면보다 크면 부드러운 효과 없이 이동합니다
    map.panTo(moveLatLon);
}
