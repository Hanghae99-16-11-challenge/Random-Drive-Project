//0 : 일반 길찾기 , 1 : 랜덤 + 범위 , 2 : 랜덤 + 목적지
var pathType = 0;
var StartPoint="";
var DestinationPoint="";
var randomRadius = 1;

var startInfoMark = {mark : new kakao.maps.Marker(), infoWin : new kakao.maps.InfoWindow()};
var endInfoMark = {mark : new kakao.maps.Marker(), infoWin : new kakao.maps.InfoWindow()};


function reset()
{
    if (document.getElementById('app_ChooseType').classList.contains("disabled"))
        document.getElementById('app_ChooseType').classList.remove("disabled");

    if (!document.getElementById('app_ChoosePath').classList.contains("disabled"))
        document.getElementById('app_ChoosePath').classList.add ("disabled");

    if (!document.getElementById('app_GuidPath').classList.contains("disabled"))
        document.getElementById('app_GuidPath').classList.add ("disabled");

    if (document.getElementById('input-start').classList.contains("disabled"))
        document.getElementById('input-start').classList.remove("disabled");

    if (document.getElementById('input-Destination').classList.contains("disabled"))
        document.getElementById('input-Destination').classList.remove("disabled");

    if (document.getElementById('input-radius').classList.contains("disabled"))
        document.getElementById('input-radius').classList.remove("disabled");

    if (!document.getElementById('input_SavePath').classList.contains("disabled"))
        document.getElementById('input_SavePath').classList.add("disabled");
}
function onClick_NormalPath()
{
    if (!document.getElementById('app_ChooseType').classList.contains("disabled"))
    {
        document.getElementById('app_ChooseType').classList.add("disabled");
        document.getElementById('app_ChoosePath').classList.remove("disabled");
    }

    if (!document.getElementById('input-radius').classList.contains("disabled"))
        document.getElementById('input-radius').classList.add("disabled");

    pathType = 0;
}
function onClick_RadiusPath()
{
    if (!document.getElementById('app_ChooseType').classList.contains("disabled"))
    {
        document.getElementById('app_ChooseType').classList.add("disabled");
        document.getElementById('app_ChoosePath').classList.remove("disabled");
    }

    if (!document.getElementById('input-Destination').classList.contains("disabled"))
        document.getElementById('input-Destination').classList.add("disabled");

    pathType = 1;
}
function onClick_DestinationPath()
{
    if (!document.getElementById('app_ChooseType').classList.contains("disabled"))
    {
        document.getElementById('app_ChooseType').classList.add("disabled");
        document.getElementById('app_ChoosePath').classList.remove("disabled");
    }

    pathType = 2;
}
function onClick_StartGuid()
{
    if (!document.getElementById('app_ChoosePath').classList.contains("disabled"))
    {
        document.getElementById('app_GuidPath').classList.remove("disabled");
        document.getElementById('app_ChoosePath').classList.add("disabled");
    }

    RemoveInfoMart(startInfoMark);
    RemoveInfoMart(endInfoMark);

    switch (pathType)
    {
        case 0:
        {
            {
                fetch('/api/route?originAddress=' + StartPoint  + '&destinationAddress=' + DestinationPoint)
                    .then(response => response.json())
                    .then(data => {
                        // data는 KakaoRouteAllResponseDto 객체
                        // clearPolylines(); // 기존의 선들을 모두 제거

                        calculateCurrectToPoint(data);//디버그 - 정보 확인용
                        pathData = data;
                        startNavi();

                        if (!map) {
                            map = new kakao.maps.Map(document.getElementById('map'), {
                                level: 3
                            });
                        }
                    })
                    .catch(except =>
                    {
                        alert("길을 찾지 못함 " + except.message);
                        console.warn(except.message + "\n" + except);
                        reset();
                    });
            }
            break;
        }
        case 1:
        {
            {
                fetch('/api/all-random-route?originAddress=' + StartPoint  + '&redius=' + randomRadius)
                    .then(response => response.json())
                    .then(data => {
                        // data는 KakaoRouteAllResponseDto 객체
                        // clearPolylines(); // 기존의 선들을 모두 제거

                        calculateCurrectToPoint(data);//디버그 - 정보 확인용
                        pathData = data;
                        startNavi();

                        if (!map) {
                            map = new kakao.maps.Map(document.getElementById('map'), {
                                level: 3
                            });
                        }

                                           })
                    .catch(except =>
                    {
                        alert("길을 찾지 못함 " + except.message);
                        console.warn(except.message + "\n" + except);
                        reset();
                    });
            }
            break;
        }
        case 2:
        {
            {
                console.log('/api/random-route?originAddress=' + StartPoint  + '&destinationAddress=' + DestinationPoint + '&redius=' + randomRadius);

                fetch('/api/random-route?originAddress=' + StartPoint  + '&destinationAddress=' + DestinationPoint + '&redius=' + randomRadius)
                    .then(response => response.json())
                    .then(data => {

                        console.log(data);

                        // data는 KakaoRouteAllResponseDto 객체
                        // clearPolylines(); // 기존의 선들을 모두 제거

                        pathData = data;
                        startNavi();

                        if (!map) {
                            map = new kakao.maps.Map(document.getElementById('map'), {
                                level: 3
                            });
                        }

                                            })
                    .catch(except =>
                    {
                        alert("길을 찾지 못함 " + except.message);
                        console.warn(except.message + "\n" + except);
                        reset();
                    });
            }
            break;
        }
    }

    //=============== 현위치 업데이트를 시작하자마자 종료시킴 + 경로 저장 기능 주석 처리
    //=============== 중복 되는 요소 함수화 시키기
}

// 0 -> 출발점 , 1 -> 목표지 , 현위치 버튼 눌렀을때
function onClick_CurrectLocation(type)
{
    navigator.geolocation.getCurrentPosition(function(position) {

        var lat = position.coords.latitude, // 위도
            lon = position.coords.longitude; // 경도


        fetch(
            'https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=' + lon + '&y=' + lat,
            {
                headers: { Authorization: 'KakaoAK 0c3c82e2bab1baa630c741b2c9f72e3c' },
            }
        )
            .then((response) => response.json())
            .then((data) => {
                if (data.documents && data.documents.length > 0) {
                    console.log(" -> " + data.documents[0].address_name);

                    switch (type)
                    {
                        case 0:
                        {
                            EditMark(startInfoMark.mark, startInfoMark.infoWin, lat, lon, "출발점");
                            panTo(lat, lon);
                            StartPoint = data.documents[0].address_name;
                            break;
                        }//출발점
                        case 1:
                        {
                            EditMark(endInfoMark.mark, endInfoMark.infoWin, lat, lon, "도착점");
                            panTo(lat, lon);
                            DestinationPoint = data.documents[0].address_name;
                            break;
                        }//도착점
                    }

                } else {
                    throw new Error('Could not find address for this coordinates.');
                }
            })
            .catch(e => {
                console.warn(e.message);
            });
    });
}
function RemoveInfoMart(infoMark)
{
    if (infoMark != null)
    {
        if (infoMark.mark == null) return;

        infoMark.mark.setMap(null);
        infoMark.infoWin.close();
    }
}
// 주소 검색창 띄우고 , 마크로 표시하기
function onClick_StartPoint()
{
    new daum.Postcode({
        oncomplete: function(data) {
            StartPoint = data.address;

            fetch('https://dapi.kakao.com/v2/local/search/address.json?query=' + StartPoint,{
                headers: {
                    'Authorization': 'KakaoAK 0c3c82e2bab1baa630c741b2c9f72e3c'
                }
            })
                .then(response => response.json())
                .then(data =>
                {
                    EditMark(startInfoMark.mark, startInfoMark.infoWin, data.documents[0].y, data.documents[0].x, "출발점");
                    panTo(data.documents[0].y, data.documents[0].x);
                });
        }
    }).open();
}
// 주소 검색창 띄우고 , 마크로 표시하기
function onClick_DestinationPoint()
{
    new daum.Postcode({
        oncomplete: function(data) {
            console.log(data.address);
            DestinationPoint = data.address;

            fetch('https://dapi.kakao.com/v2/local/search/address.json?query=' + DestinationPoint,{
                headers: {
                    'Authorization': 'KakaoAK 0c3c82e2bab1baa630c741b2c9f72e3c'
                }
            })
                .then(response => response.json())
                .then(data =>
                {
                    EditMark(endInfoMark.mark, endInfoMark.infoWin, data.documents[0].y, data.documents[0].x, "도착점");
                    panTo(data.documents[0].y, data.documents[0].x);
                });
        }
    }).open();
}
function onClick_SelectRadius()
{
    randomRadius = document.getElementById('radiusSelector').value;

    console.log("Select : " + document.getElementById('radiusSelector').value);
}

function update_GuidInfo()
{
    let data = getGuidPoint(false);
    //data -> 다음 안내 지점
    document.getElementById('guid-Distance').innerText = "전방 " + nextGuidDistacne.toFixed(1) + "m 에서 " + data.guidance;
    document.getElementById('guid-EnterTime').innerText = "다음 안내 까지 : " + nextGuidDuration.toFixed(1) + "s";
    document.getElementById('guid-Des-Distance').innerText = pathLeftDistance.toFixed(1) + "m";
    document.getElementById('guid-Des-Time').innerText = pathLeftDuration.toFixed(1) + "s";
}

function onClick_StopNavi()
{
    reset();
    stopNavi();
}
function onClick_ToggleLocUpdate()
{
    if (intervalId >= 0)
    {
        stopCoroutine();
        console.warn("위치 업데이트 종료");
    }else
    {
        startCorutine();
        console.warn("위치 업데이트 시작");
    }
}

function onClick_AskSave()
{
    if (!document.getElementById('app_GuidPath').classList.contains("disabled"))
        document.getElementById('app_GuidPath').classList.add("disabled");

    if (document.getElementById('input_SavePath').classList.contains("disabled"))
        document.getElementById('input_SavePath').classList.remove("disabled");
}
function onClick_SavePath()
{
    console.log("try save path");

    {
        const auth = getToken();
        fetch('/api/routes', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': auth // 인증 토큰을 Authorization 헤더에 추가
            },
            body: JSON.stringify({
                requestData: pathData, // KakaoRouteAllResponseDto 객체
                originAddress: StartPoint,
                destinationAddress: DestinationPoint
            })
        })
    }

    onClick_StopNavi();
}