
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

var positionMark = new kakao.maps.Marker();
var positionText = new kakao.maps.InfoWindow();
var naviInfoMark = new kakao.maps.Marker();
var naviInfoText = new kakao.maps.InfoWindow();


function EditMark(mark , infoWin , lat, log, text)
{
    // mark.position = new kakao.maps.LatLng(lat, log);
    mark.setPosition(new kakao.maps.LatLng(lat, log));

    infoWin.setPosition(new kakao.maps.LatLng(lat, log));
    infoWin.setContent(text);

    mark.setMap(map);
    infoWin.open(map, mark);
}

kakao.maps.event.addListener(map, 'click', function (mouseEvent)
{
    // 클릭한 위도, 경도 정보를 가져옵니다
    var latlng = mouseEvent.latLng;
    var message = '클릭한 위치의 위도는 ' + latlng.getLat() + ' 이고, ';
    message += '경도는 ' + latlng.getLng() + ' 입니다';

    console.log(message);

    // // 지도를 클릭한 위치에 표출할 마커입니다
    // var marker = new kakao.maps.Marker({
    //     // 지도 중심좌표에 마커를 생성합니다
    //     position: map.getCenter()
    // });
    //
    // // 지도에 마커를 표시합니다
    // marker.setMap(map);
    //
    // // 마커 위치를 클릭한 위치로 옮깁니다
    // marker.setPosition(latlng);

    EditMark(positionMark, positionText, latlng.getLat(), latlng.getLng(), '클릭한 위치');

    if(pathData == null)
    {
        console.log("길 찾기 정보 없음");
    }else
    {
        // console.log("길 찾기 정보 있음 , 소요 시간 : " + pathData.routes[0].summary.duration + "s");
        //작동함!

        // 가장 가까운 지점을 찾고 , 앞에있는지(지나갔는지) 확인
        // pathData.routes[0].sections[0].guides
        //routes -> 경로 , sections -> 구간(경유지마다 추가)

        var closeData = {
            data: pathData.routes[0].sections[0].guides[0],
            squaredLength: -1
        }


        console.log("구간 갯수 : " + pathData.routes[0].sections.length);

        // for (let sec in pathData.routes[0].sections)//-------for in 이 안되는건가?

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


        EditMark(naviInfoMark, naviInfoText, closeData.data.y, closeData.data.x, '가장 가까운 안내 지점 (' + (closeData.squaredLength * 1000).toFixed(2) + 'm)');

        //----- 마지막 안내 지점을 저장해서 (기본값 0) , 다음 안내 지점에 근접하면 마지막 안내지점 인덱스 증가해 다음 안내 보여줌
    }
});//지도 클릭시