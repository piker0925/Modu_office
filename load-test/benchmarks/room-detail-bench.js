/**
 * 회의실 상세 조회 마이크로 벤치마크
 * 목적: GET /rooms/{id} 단독 응답시간 측정 (캐시 적용 전/후 비교용)
 * 실행: k6 run -e BASE_URL=http://localhost/api benchmarks/room-detail-bench.js
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost/api';

const roomDetailDuration = new Trend('room_detail_duration', true);
const roomDetailErrors   = new Rate('room_detail_errors');

export const options = {
    stages: [
        { duration: '15s', target: 1000 }, // 램프업
        { duration: '60s', target: 1000 }, // 실측 구간
        { duration: '10s', target: 0    }, // 램프다운
    ],
    thresholds: {
        room_detail_duration: ['p(95)<5000'],
        room_detail_errors:   ['rate<0.05'],
    },
};

// setup(): 테스트 전 한 번 실행 — 로그인 후 유효한 room ID 목록 수집
export function setup() {
    // 1. 로그인하여 토큰 획득
    const loginRes = http.post(`${BASE_URL}/auth/user/login`, JSON.stringify({
        email: 'loadtest-user-0001@test.com',
        password: 'Test1234!'
    }), { headers: { 'Content-Type': 'application/json' } });

    let token = null;
    try {
        token = loginRes.json('accessToken');
    } catch (e) {}

    if (!token) {
        console.error(`로그인 실패: ${loginRes.status} ${loginRes.body}`);
    }

    // 2. 인증 헤더로 회의실 검색 → 실제 ID 수집
    const headers = token
        ? { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' }
        : { 'Content-Type': 'application/json' };

    const res = http.get(`${BASE_URL}/rooms/search?lat=37.5665&lng=126.978&size=50`, { headers });
    let roomIds = [];
    try {
        const body = res.json();
        const pageData = body.data || body;
        const rooms = Array.isArray(pageData) ? pageData : (pageData.content || []);
        roomIds = rooms.map(r => r.id).filter(Boolean);
    } catch (e) {}

    // fallback: DB에서 확인한 실제 ID 범위 직접 사용
    if (roomIds.length === 0) {
        const minId = parseInt(__ENV.ROOM_MIN_ID || '10501');
        const maxId = parseInt(__ENV.ROOM_MAX_ID || '11000');
        for (let i = minId; i <= maxId; i++) roomIds.push(i);
        console.log(`fallback: ID 범위 ${minId}~${maxId} 사용`);
    }

    console.log(`수집된 roomId 수: ${roomIds.length}, 샘플: ${roomIds.slice(0, 5)}`);
    return { roomIds, token };
}

export default function (data) {
    const { roomIds, token } = data;
    const roomId = roomIds[Math.floor(Math.random() * roomIds.length)];

    const headers = token ? { 'Authorization': `Bearer ${token}` } : {};

    const start = Date.now();
    const res = http.get(`${BASE_URL}/rooms/${roomId}`, {
        headers,
        tags: { endpoint: 'room_detail' },
    });
    const duration = Date.now() - start;

    roomDetailDuration.add(duration);
    roomDetailErrors.add(res.status !== 200);

    check(res, {
        'status 200': (r) => r.status === 200,
    });

    sleep(0.1); // 짧은 간격으로 최대한 많이 측정
}
