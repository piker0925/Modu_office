-- 위치 기반 회의실 검색(Bounding Box 필터)의 성능 개선을 위한 인덱스
-- RoomRepositoryCustomImpl.searchRooms()에서 office.latitude BETWEEN, office.longitude BETWEEN 조건에 사용됨
CREATE INDEX IF NOT EXISTS idx_office_latitude  ON office (latitude);
CREATE INDEX IF NOT EXISTS idx_office_longitude ON office (longitude);
