# 🚗 Random-Drive-Project


팀원 중 한 명이 늘 같은 길만 다니는 게 지겨워서, “어디로 갈지 모르게 그냥 드라이브를 떠나고 싶을 때가 있다.” 라고 던진 한마디에서 모든 게 시작됐습니다. 길찾기 기능을 구현하는 것 또한 챌린지가 되리라 짐작했고, 이러한 도전을 저희 팀이 받아들이며 [RanDrive]가 탄생하게 되었습니다. 

<br>
<br>

## 📋 프로젝트 소개
운전을 모험으로 변화시키세요. “RanDrive”는 평범한 길찾기를 넘어서, 새롭고 뜻밖의 경로를 제안합니다. 기존의 최적화된 경로를 벗어나, 직선, 지그재그, 선형 등 예측할 수 없는 다양한 경로를 생성, 당신의 일상에 작은 모험을 선사합니다.

<br>
<br>

## 🕰️ 개발 기간
* 23.10.04 ~ 23.11.15

<br>
<br>

### 🧑‍🤝‍🧑 팀 소개
- **Spring 챌린지팀** 

<br>

- 이해찬(팀장) - https://github.com/haechanlee96
- 최수경 - https://github.com/teresa881016
- 강현욱 - https://github.com/rkdgusdnr99
- 한민수 - https://github.com/mshan2923
<br>

- **팀 노션** - https://haechanlee96.notion.site/Random-Drive-Project-S-A-e95829474db949508cef3599e8680b5c?pvs=4
- **팀 브로셔** - https://haechanlee96.notion.site/RanDrive-451af55e6e4a4c0db1456b0231e475ef?pvs=4

<br>
<br>

## ⚙️ Project Architecture

![image](https://github.com/Hanghae99-16-11-challenge/Random-Drive-Project/assets/131975479/f2d1e1a3-943c-47ab-8773-86a6c6cedee7)


## 🗃 Tech Stack

![image](https://github.com/Hanghae99-16-11-challenge/Random-Drive-Project/assets/131975479/25aeddfc-ed8d-4914-a300-166271d216d4)

<br>
<br>

## 🖋 ERD

![image](https://github.com/Hanghae99-16-11-challenge/Random-Drive-Project/assets/131975479/4f5f6851-13e3-4bc8-a640-76f03c03ac33)

<br>
<br>


## 🔎 API
![image](https://github.com/Hanghae99-16-11-challenge/Random-Drive-Project/assets/131975479/349d5fb0-ed08-49fb-ba06-b0a8628941b8)



<br>
<br>

## ⚙ 주요 기능

<details>
<summary>01.카카오API 활용</summary>
<div markdown="1">

- 사용자가 검색하는 실제 주소의 좌표를 정확하게 제공
- 키워드로 검색하는 경우, 주소와 장소명 제공
- 전국의 도로 데이터, 실시간 교통 정보 상태 등을 활용 가능
- 카카오api 길찾기 내부 알고리즘 활용 가능
  
</div>
</details>



<details>
<summary>02.길찾기 알고리즘</summary>
<div markdown="1">

- 실시간 도로 정보를 바탕으로 길찾기 경로 생성
- 경유지 좌표 정보를 가지고 랜덤 경로 생성
- 직선,평탄 지그재그 순환형 모양에 가까운 경로 생성
- 전체 경로 조회는 날짜,출발지,도착지 형태로 조회
- 상세 조회를 통해 같은 경로 안내
  
</div>
</details>



<details>
<summary>03.실시간 사용자 위치</summary>
<div markdown="1">

- 사용자 위치(gps)에 따른 거리계산 알고리즘 적용
- 사용자 위치에 따른 길찾기 가이드 제시
- 기존에 생성한 경로에서 일정 범위 이상 벗어나면 경로 재생성
  
</div>
</details>



<details>
<summary>04.음성 안내</summary>
<div markdown="1">

- TTS를 활용한 음성 가이드 제공
  
</div>
</details>












