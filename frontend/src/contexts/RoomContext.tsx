import { createContext, useContext, useState, useEffect, type ReactNode } from 'react';
import { type Room } from '../features/rooms/RoomCard';

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

interface RoomContextType {
    rooms: Room[];
    addRoom: (room: Omit<Room, 'id' | 'isAvailable'>) => void;
    deleteRoom: (id: string) => void;
}

const RoomContext = createContext<RoomContextType | undefined>(undefined);

export function RoomProvider({ children }: { children: ReactNode }) {
    const [rooms, setRooms] = useState<Room[]>(() => {
        const storedRooms = localStorage.getItem('rooms');
        if (storedRooms) {
            return JSON.parse(storedRooms);
        }
        // Initialize with mock data if storage is empty
        localStorage.setItem('rooms', JSON.stringify(MOCK_ROOMS));
        return MOCK_ROOMS;
    });

    useEffect(() => {
        localStorage.setItem('rooms', JSON.stringify(rooms));
    }, [rooms]);

    const addRoom = (roomData: Omit<Room, 'id' | 'isAvailable'>) => {
        const newRoom: Room = {
            ...roomData,
            id: crypto.randomUUID(), // Generate unique ID
            isAvailable: true, // Default to available
        };
        setRooms(prev => [...prev, newRoom]);
    };

    const deleteRoom = (id: string) => {
        setRooms(prev => prev.filter(room => room.id !== id));
    };

    return (
        <RoomContext.Provider value={{ rooms, addRoom, deleteRoom }}>
            {children}
        </RoomContext.Provider>
    );
}

export function useRooms() {
    const context = useContext(RoomContext);
    if (context === undefined) {
        throw new Error('useRooms must be used within a RoomProvider');
    }
    return context;
}
