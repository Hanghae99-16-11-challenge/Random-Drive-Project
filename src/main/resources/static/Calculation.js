
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
        alert("현제위치와 출발지간의 직선거리 : " + calculateDistance(C_lat, C_lon, let_ori, lon_ori) + "km" +
            "\n현제위치와 도착지 간의 직선거리 : " + calculateDistance(C_lat, C_lon, lat_des, lon_des) + "km" +
            "\n경로 길이 : " + data.routes[0].summary.distance +"m");

    });


}