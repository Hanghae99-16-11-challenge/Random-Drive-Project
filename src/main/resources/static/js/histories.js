// 로그아웃 버튼 클릭 이벤트
// 추가한 코드
const box_pst = document.querySelectorAll(".logout-button")[0].offsetTop
document.getElementsByClassName('logout-button')[0].addEventListener('click', function () {
    logout();
    // 주가한 코드
    window.scrollTo({left: 0, top: box_pst - 100})
});

// 주요 기능
$(document).ready(function () {
    const auth = getToken();

    if (auth !== undefined && auth !== '') {
        $.ajaxPrefilter(function (options, originalOptions, jqXHR) {
            jqXHR.setRequestHeader('Authorization', auth);
        });

        // API 호출
        fetch('/api/routes', {
            method: 'GET',
            headers: {
                'Authorization': auth
            }
        })
            .then(response => response.json())
            .then(data => {
                // 받아온 데이터를 화면에 렌더링
                renderHistory(data);

            })
            .catch(error => {
                console.error('Error:', error);
            });
    } else {
        window.location.href = host + '/view/user/login-page';
    }
});

// 보여질 정보 가져오기
function renderHistory(histories) {
    const tbody = document.getElementById('historyTableBody');
    // historiesMarker(histories);

    try {
        histories.forEach(history => {
            const row = document.createElement('tr');
            const formattedOriginAddress = extractOriginAddressParts(history.originAddress);
            const formattedDestinationAddress = extractDestinationAddressParts(history.destinationAddress);

            //마커용
            const markerOriginAddress = history.originAddress;
            const markerDestinationAddress = history.destinationAddress;

            row.innerHTML = `
                <td>${extractDateFromDateTime(history.createdAt)}</td>
                <td>${formattedOriginAddress}</td>
                <td>${formattedDestinationAddress}</td>
            `;

            row.addEventListener('click', function () {
                // 클릭한 행의 route_id를 사용하여 원하는 동작 수행
                window.location.href = 'navi/' + 'save' + '/' + history.route_id + "/blank/blank/0/0/blank";

                if (markerOriginAddress !== null || markerDestinationAddress !== null) {
                    historiesMarker(markerOriginAddress, markerDestinationAddress);
                }
            });

            console.log(history.mapType);

            row.setAttribute('data-maptype', history.mapType);

            tbody.appendChild(row);

        });




        if (histories.length === 0) {
            console.log("기록이 없어요.");
        }
    } catch (e) {
        console.warn(e.message);
        console.warn(histories.msg);
        logout();
    }
}

// 날자만 뜨도록 설정
function extractDateFromDateTime(dateTimeString) {
    const dateTime = new Date(dateTimeString);
    const year = dateTime.getFullYear();
    const month = String(dateTime.getMonth() + 1).padStart(2, '0'); // 월은 0부터 시작하므로 1을 더하고 문자열로 변환
    const day = String(dateTime.getDate()).padStart(2, '0'); // 날짜를 문자열로 변환
    return `${month}-${day}`;
}

// 킬로미터로 변환
function formatDistance(distance) {
    const kilometers = distance / 1000; // 미터를 킬로미터로 변환
    return kilometers.toFixed(1) + ' km'; // 소수점 첫 번째 자리까지 나타내기
}

// 시간 분 형식으로 시간을 변환
function formatDuration(duration) {
    const hours = Math.floor(duration / 3600); // 초를 시간으로 변환
    const minutes = Math.floor((duration % 3600) / 60); // 초를 분으로 변환하고 소수점 첫 번째 자리까지 나타내기
    return `${hours}시간${minutes}분`;
}

// 주소 짧게 줄임
function extractOriginAddressParts(address) {
    const parts = address.split(' ');
    if (parts.length >= 3) {
        return `${parts[1]} ${parts[2]}`;
    }
    return address; // 두 번째와 세 번째 부분이 없는 경우 전체 주소 반환
}

function extractDestinationAddressParts(address) {
    const parts = address.split(' ');
    const regex = /\d$/;
    if (parts.length >= 4) {
        const str = parts[3];
        const endsWithNumber = regex.test(str);
        if (endsWithNumber)
            return `${parts[1]} ${parts[2]}`
        else
            return `${parts[1]} ${parts[2]} ${parts[3]}`;
    }
    return address; // 두 번째와 세 번째 부분이 없는 경우 전체 주소 반환
}

// 로그아웃
function logout() {
    // 토큰 삭제
    Cookies.remove('Authorization', {path: '/'});
    // window.location.reload(); // 현재 페이지 리로드
    window.location.href = '/view/user/login-page';
}

// 토큰 가져오기
function getToken() {

    let auth = Cookies.get('Authorization');

    if (auth === undefined) {
        return '';
    }

    // kakao 로그인 사용한 경우 Bearer 추가
    if (auth.indexOf('Bearer') === -1 && auth !== '') {
        auth = 'Bearer ' + auth;
    }
    return auth;
}

// 히스토리 마커
function historiesMarker(formattedOriginAddress, formattedDestinationAddress, row) {

    // 주소-좌표 변환 객체를 생성합니다
    var geocoder = new kakao.maps.services.Geocoder();

    var addresses = [formattedOriginAddress, formattedDestinationAddress];
    addresses.forEach(address => {
        geocoder.addressSearch(address, function (result, status) {
            // 정상적으로 검색이 완료됐으면
            if (status === kakao.maps.services.Status.OK) {
                historiesMakeMarker(result);
            }
        });
    });
}


function historiesMakeMarker(result) {
    // 출발지 도착지 마커 표시하기
    var let_ori = result[0].y;
    var lon_ori = result[0].x;
    var lat_des = result[1].y;
    var lon_des = result[1].x;

    var positions = [
        {
            title: '출발',
            latlng: new kakao.maps.LatLng(let_ori, lon_ori),
            image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/red_b.png'
            //"https://cdn-icons-png.flaticon.com/512/6213/6213694.png"
        },
        {
            title: '도착',
            latlng: new kakao.maps.LatLng(lat_des, lon_des),
            image: 'https://t1.daumcdn.net/localimg/localimages/07/mapapidoc/blue_b.png'
            //"https://cdn-icons-png.flaticon.com/512/4856/4856582.png"
        }

    ]
// 마커를 지도에 표시하기
    for (var i = 0; i < positions.length; i++) {
        // 마커 이미지 크기
        var imageSize = new kakao.maps.Size(30, 40);
        var markerImage = new kakao.maps.MarkerImage(positions[i].image, imageSize);
        var marker = new kakao.maps.Marker({
            map: map,
            position: positions[i].latlng,
            title: positions[i].title,
            image: markerImage,
        })
    }

// 경유지 마커 표시
    var imageWay = 'https://file.notion.so/f/f/0bb6a7f0-5b10-43e2-ad69-0657263c6dff/ccc1ee90-0fa2-4d98-a1a6-80f40600896f/%EA%B2%BD%EC%9C%A0%EC%A7%80-01.png?id=6eaca2df-ea86-468a-bf07-51df643bf11b&table=block&spaceId=0bb6a7f0-5b10-43e2-ad69-0657263c6dff&expirationTimestamp=1698933600000&signature=256uSQ0yL4SEQ_vhA4Ejx0_PTykB_Nj-2KJev6apoOI&downloadName=%EA%B2%BD%EC%9C%A0%EC%A7%80-01.png';
    var imageSize = new kakao.maps.Size(40, 35);
    // 마커 이미지를 생성합니다
    var markerImage = new kakao.maps.MarkerImage(imageWay, imageSize);

    // 경유지 마커 표시하기
    for (var i = 0; i < data.routes[0].sections.length; i++) {
        var section = data.routes[0].sections[i];
        for (var j = 0; j < section.guides.length; j++) {
            var guide = section.guides[j];
            if (guide.type === 1000) {
                var lat_way = guide.y;
                var lon_way = guide.x;

                var latlng = new kakao.maps.LatLng(lat_way, lon_way)

                var marker = new kakao.maps.Marker({
                    map: map,
                    position: latlng,
                    image: markerImage
                });
            }
        }
    }
}