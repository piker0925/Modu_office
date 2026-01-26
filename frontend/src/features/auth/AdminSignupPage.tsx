import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import Input from '../../components/Input';
import './SignupPage.css'; // Reuse existing styles

export default function AdminSignupPage() {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [adminCode, setAdminCode] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setLoading(true);

        // Security check
        if (adminCode !== 'admin1234') {
            alert('관리자 인증 코드가 올바르지 않습니다.');
            setLoading(false);
            return;
        }

        try {
            await new Promise(resolve => setTimeout(resolve, 1000));

            const users = JSON.parse(localStorage.getItem('users') || '[]');
            const existingUser = users.find((u: any) => u.email === email);

            if (existingUser) {
                alert('이미 가입된 이메일 주소입니다.');
                setLoading(false);
                return;
            }

            const newUser = { name, email, password, role: 'ADMIN' };
            users.push(newUser);
            localStorage.setItem('users', JSON.stringify(users));

            login({
                id: email,
                name: name,
                role: 'ADMIN'
            }, 'mock-jwt-token-admin');
            navigate('/rooms');
        } catch (error) {
            console.error(error);
            setLoading(false);
        }
    };

    return (
        <div className="card signup-card" style={{ borderColor: 'var(--color-accent)' }}>
            <div className="text-center mb-lg">
                <h1 className="signup-title font-bold mb-sm text-accent">관리자 가입</h1>
                <p className="signup-subtitle text-muted text-sm">회사 관리자 계정을 생성합니다</p>
            </div>

            <form onSubmit={handleSubmit}>
                <Input
                    label="관리자 이름"
                    type="text"
                    placeholder="관리자 홍길동"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                    fullWidth
                />

                <Input
                    label="이메일 주소"
                    type="email"
                    placeholder="admin@company.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    fullWidth
                />

                <Input
                    label="비밀번호"
                    type="password"
                    placeholder="비밀번호를 입력해주세요"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    fullWidth
                />

                <Input
                    label="인증 코드"
                    type="password"
                    placeholder="관리자 인증 코드를 입력하세요"
                    value={adminCode}
                    onChange={(e) => setAdminCode(e.target.value)}
                    required
                    fullWidth
                />

                <button
                    type="submit"
                    className="btn btn-primary signup-btn w-full mt-md text-md py-3"
                    disabled={loading}
                    style={{ background: 'var(--color-accent)', color: '#000' }}
                >
                    {loading ? '인증 및 가입 중...' : '관리자 가입하기'}
                </button>
            </form>

            <div className="text-center mt-lg text-sm signup-footer">
                <Link to="/login" className="text-muted hover:text-white">
                    ← 로그인 화면으로 돌아가기
                </Link>
            </div>
        </div>
    );
}
