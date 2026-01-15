import { useState } from 'react';
import RoomCard, { type Room } from './RoomCard';
import './RoomsListPage.css';

// Mock Data
const MOCK_ROOMS: Room[] = [
    {
        id: '1',
        name: 'Galaxy Conference Hall',
        location: 'Floor 10, East Wing',
        capacity: 20,
        equipment: ['Projector', 'Video Conf', 'Whiteboard'],
        imageUrl: 'https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&q=80&w=1000',
        isAvailable: true,
    },
    {
        id: '2',
        name: 'Nebula Meeting Room',
        location: 'Floor 10, West Wing',
        capacity: 8,
        equipment: ['TV', 'Whiteboard'],
        imageUrl: 'https://images.unsplash.com/photo-1577412647305-991150c7d163?auto=format&fit=crop&q=80&w=1000',
        isAvailable: false,
    },
    {
        id: '3',
        name: 'Cosmos Focus Pod',
        location: 'Floor 11, Central',
        capacity: 4,
        equipment: ['Display', 'Soundproofing'],
        imageUrl: 'https://images.unsplash.com/photo-1517502884422-41eaead166d4?auto=format&fit=crop&q=80&w=1000',
        isAvailable: true,
    },
    {
        id: '4',
        name: 'Starlight Studio',
        location: 'Floor 11, North Wing',
        capacity: 12,
        equipment: ['Projector', 'Green Screen', 'Mac Studio'],
        imageUrl: 'https://images.unsplash.com/photo-1505409859467-3a796fd5798e?auto=format&fit=crop&q=80&w=1000',
        isAvailable: true,
    }
];

export default function RoomsListPage() {
    const [filter, setFilter] = useState<'ALL' | 'AVAILABLE'>('ALL');

    const filteredRooms = filter === 'ALL'
        ? MOCK_ROOMS
        : MOCK_ROOMS.filter(room => room.isAvailable);

    return (
        <div className="rooms-page">
            <div className="rooms-page-header">
                <h1 className="text-3xl font-bold text-gradient">공간 예약</h1>
                <p className="rooms-subtitle">회의에 적합한 공간을 찾아보세요</p>
            </div>

            <div className="filters-bar">
                <button
                    className={`filter-btn ${filter === 'ALL' ? 'active' : ''}`}
                    onClick={() => setFilter('ALL')}
                >
                    전체
                </button>
                <button
                    className={`filter-btn ${filter === 'AVAILABLE' ? 'active' : ''}`}
                    onClick={() => setFilter('AVAILABLE')}
                >
                    예약 가능
                </button>
            </div>

            <div className="rooms-grid">
                {filteredRooms.map(room => (
                    <RoomCard key={room.id} room={room} />
                ))}
            </div>
        </div>
    );
}
