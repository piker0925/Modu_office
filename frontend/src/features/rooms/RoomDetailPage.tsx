import { useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import './RoomDetailPage.css';

// Mock Data (In real app, fetch by ID)
const ROOM_DETAILS = {
    id: '1',
    name: 'Galaxy Conference Hall',
    location: '10층 동관',
    capacity: 20,
    description: '최첨단 화상 회의 장비와 탁 트인 도시 전망을 갖춘 넓은 컨퍼런스 홀입니다. 이사회 회의나 대규모 팀 프레젠테이션에 적합합니다.',
    equipment: ['4K 프로젝터', 'Polycom 화상 장비', '디지털 화이트보드', '음향 시스템', '인체공학 의자'],
    imageUrl: 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80&w=1000',
    pricePerHour: 50, // Mock price generic unit
};

const TIME_SLOTS = [
    { time: '09:00', available: true },
    { time: '10:00', available: false }, // Occupied
    { time: '11:00', available: true },
    { time: '13:00', available: true },
    { time: '14:00', available: true },
    { time: '15:00', available: false },
    { time: '16:00', available: true },
    { time: '17:00', available: true },
];

export default function RoomDetailPage() {
    const { id } = useParams();
    const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
    const [selectedTime, setSelectedTime] = useState<string | null>(null);

    // Fallback if room not found in mock (just use the mock object mostly)
    const room = ROOM_DETAILS;

    const handleBook = () => {
        alert(`${selectedDate} ${selectedTime}에 ${room.name} 예약이 확정되었습니다.`);
    };

    return (
        <div className="room-detail-page">
            <Link to="/rooms" className="back-link">← 목록으로 돌아가기</Link>

            <div className="room-title-section">
                <h1 className="text-3xl font-bold text-gradient">{room.name}</h1>
                <p className="text-muted">📍 {room.location} • 👥 수용인원: {room.capacity}명</p>
            </div>

            <div className="room-detail-grid">
                {/* Left Column: Image & Info */}
                <div className="room-main-content">
                    <div className="room-hero-image-wrapper">
                        <img src={room.imageUrl} alt={room.name} className="room-hero-image" />
                    </div>

                    <div className="info-section">
                        <h2 className="section-title">공간 소개</h2>
                        <p className="text-muted" style={{ lineHeight: '1.6' }}>{room.description}</p>
                    </div>

                    <div className="info-section">
                        <h2 className="section-title">시설 및 장비</h2>
                        <div className="equipment-list">
                            {room.equipment.map((item, idx) => (
                                <span key={idx} className="badge" style={{ padding: '0.5rem 0.75rem', fontSize: '0.9rem' }}>
                                    ✅ {item}
                                </span>
                            ))}
                        </div>
                    </div>
                </div>

                {/* Right Column: Booking Widget */}
                <div className="booking-sidebar">
                    <div className="booking-widget">
                        <h2 className="widget-title">이 공간 예약하기</h2>

                        <div className="date-picker-wrapper">
                            <label className="input-label text-sm font-bold mb-xs block">날짜 선택</label>
                            <input
                                type="date"
                                className="input-field"
                                value={selectedDate}
                                onChange={(e) => setSelectedDate(e.target.value)}
                            />
                        </div>

                        <label className="input-label text-sm font-bold mb-xs block">시간 선택</label>
                        <div className="time-grid">
                            {TIME_SLOTS.map((slot) => (
                                <button
                                    key={slot.time}
                                    className={`time-slot ${selectedTime === slot.time ? 'selected' : ''}`}
                                    disabled={!slot.available}
                                    onClick={() => setSelectedTime(slot.time)}
                                >
                                    {slot.time}
                                </button>
                            ))}
                        </div>

                        <div className="summary-section mb-lg p-sm rounded-md" style={{ background: 'rgba(255,255,255,0.05)' }}>
                            <div className="flex-between text-sm mb-xs">
                                <span className="text-muted">선택됨:</span>
                                <span className="font-bold">{selectedDate} @ {selectedTime || '--:--'}</span>
                            </div>
                        </div>

                        <button
                            className="btn btn-primary w-full py-3"
                            disabled={!selectedTime}
                            onClick={handleBook}
                        >
                            예약 확정
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
