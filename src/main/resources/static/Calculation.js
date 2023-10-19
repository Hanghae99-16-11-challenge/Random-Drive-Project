
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

    var originAddress = document.getElementById('all-random-originAddress').value;//반경 기반 랜덤 길찾기 - 출발지

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

        alert("현제위치와 출발지간의 직선거리 : " + calculateDistance(C_lat, C_lon, let_ori, lon_ori) + "km" +
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
kakao.maps.event.addListener(map, 'click', function (mouseEvent)
{
    //navigation.html 지도 클릭시 작동
    //\n 클릭한 위도, 경도 정보를 가져옵니다
    var latlng = mouseEvent.latLng;
    var message = '클릭한 위치의 위도는 ' + latlng.getLat() + ' 이고, ';
    message += '경도는 ' + latlng.getLng() + ' 입니다';

    console.log(message);


    EditMark(positionMark, positionText, latlng.getLat(), latlng.getLng(), '클릭한 위치');

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

        // console.log("구간 갯수 : " + pathData.routes[0].sections.length);

        for (var sec = 0; sec < pathData.routes[0].sections.length; sec++)
        {
            for (var guid = 0; guid < pathData.routes[0].sections[sec].guides.length; guid++)
            {
                var guidData = pathData.routes[0].sections[sec].guides[guid];

                if (closeData.squaredLength < 0)
                {
                    closeData.data = guidData;
                    closeData.squaredLength = calculateDistance(latlng.getLat(), latlng.getLng(), guidData.y, guidData.x);
                }else
                {
                    var tempPoint = calculateDistance(latlng.getLat(), latlng.getLng(), guidData.y, guidData.x);

                    if (closeData.squaredLength > tempPoint)
                    {
                        closeData.data = guidData;
                        closeData.squaredLength = tempPoint;
                    }
                    // 이전 값과 거리비교
                }
            }
        }// 클릭한 지점에서 가장 가까운 안내 지점


        console.log('가장 가까운 안내 지점 (' + (closeData.squaredLength * 1000).toFixed(2) + 'm)');
        // EditMark(naviInfoMark, naviInfoText, closeData.data.y, closeData.data.x, '가장 가까운 안내 지점 (' + (closeData.squaredLength * 1000).toFixed(2) + 'm)');

        //----- 마지막 안내 지점을 저장해서 (기본값 0) , 다음 안내 지점에 근접하면 마지막 안내지점 인덱스 증가해 다음 안내 보여줌

        var point = getGuidPoint(true);

        console.log("다음 안내지점 => " + point.y + " , " + point.x + " => " + calculateDistance(latlng.getLat(), latlng.getLng(), point.y, point.x));

        //20m 이내로 접근한다면 다음 안내
        if (calculateDistance(latlng.getLat(), latlng.getLng(), point.y, point.x) < (20 * 0.001))
        {
            getNextGuidPoint(true);
            getGuidPoint(true);
        }
    }
});

function startNavi()
{
    getNextGuidPoint(false);
    getGuidPoint();
}

//네비게이션 안내 초기화
function clearNavi()
{
    naviInfo_SectionIndex = 0;
    naviInfo_GuidIndex = 1;

    naviInfo_State = getNextGuidPoint(false);

    naviInfoMark.setMap(null);
    naviInfoText.close();
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
                naviInfo_SectionIndex = naviInfo_SectionIndex + 1;
                naviInfo_GuidIndex = naviInfo_GuidIndex + 1;
            }

            // console.log("곧 목적지 입니다. => " + naviInfo_SectionIndex + " : " + naviInfo_GuidIndex);
            naviInfo_State = 3;
            return 3;
        }
        else if (((naviInfo_GuidIndex + 1) > (pathData.routes[0].sections[LastSectionIdex].guides.length - 1)))
        {
            // console.log("이미 목적지 입니다");
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