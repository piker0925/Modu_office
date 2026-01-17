import { Link } from 'react-router-dom';
import './RoomCard.css';

export interface Room {
    id: string;
    name: string;
    location: string;
    capacity: number;
    equipment: string[];
    imageUrl?: string;
    isAvailable: boolean;
}

interface RoomCardProps {
    room: Room;
}

export default function RoomCard({ room }: RoomCardProps) {
    return (
        <Link to={`/rooms/${room.id}`} style={{ textDecoration: 'none' }}>
            <div className="card room-card">
                <div className="room-image-wrapper">
                    {room.imageUrl ? (
                        <img src={room.imageUrl} alt={room.name} className="room-image" />
                    ) : (
                        <div className="room-image flex-center text-muted" style={{ background: '#334155' }}>
                            No Image
                        </div>
                    )}
                </div>

                <div className="room-content">
                    <div className="room-header">
                        <h3 className="room-name">{room.name}</h3>
                        <span className="room-location">📍 {room.location}</span>
                    </div>

                    <div className="room-meta">
                        <span className="badge badge-capacity">👥 {room.capacity} People</span>
                        {room.equipment.slice(0, 2).map((item, index) => (
                            <span key={index} className="badge">🔧 {item}</span>
                        ))}
                        {room.equipment.length > 2 && (
                            <span className="badge">+{room.equipment.length - 2}</span>
                        )}
                    </div>

                    <div className="room-footer">
                        <div className="status-indicator">
                            <span className={`status-dot ${room.isAvailable ? 'status-available' : 'status-occupied'}`}></span>
                            <span className={room.isAvailable ? 'text-success' : 'text-muted'}>
                                {room.isAvailable ? 'Available Now' : 'Occupied'}
                            </span>
                        </div>
                        <span className="btn btn-secondary text-xs">View Details</span>
                    </div>
                </div>
            </div>
        </Link>
    );
}
