import { useState, type FormEvent } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../contexts/AuthContext';
import Input from '../../components/Input';
import './SignupPage.css';

export default function SignupPage() {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e: FormEvent) => {
        e.preventDefault();
        setLoading(true);

        try {
            // Simulate API delay
            await new Promise(resolve => setTimeout(resolve, 1000));

            const users = JSON.parse(localStorage.getItem('users') || '[]');
            const existingUser = users.find((u: any) => u.email === email);

            if (existingUser) {
                alert('이미 가입된 이메일 주소입니다.');
                setLoading(false);
                return;
            }

            const newUser = { name, email, password };
            users.push(newUser);
            localStorage.setItem('users', JSON.stringify(users));

            login({
                id: email,
                name: name,
                role: 'USER'
            }, 'mock-jwt-token-user');
            navigate('/rooms');
        } catch (error) {
            console.error(error);
            setLoading(false);
        }
    };

    return (
        <div className="card signup-card">
            <div className="text-center mb-lg">
                <h1 className="signup-title font-bold mb-sm">회원가입</h1>
                <p className="signup-subtitle text-muted text-sm">오늘 바로 Modu Office를 시작하세요</p>
            </div>

            <form onSubmit={handleSubmit}>
                <Input
                    label="이름"
                    type="text"
                    placeholder="홍길동"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    required
                    fullWidth
                />

                <Input
                    label="이메일 주소"
                    type="email"
                    placeholder="name@company.com"
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

                <button
                    type="submit"
                    className="btn btn-primary signup-btn w-full mt-md text-md py-3"
                    disabled={loading}
                >
                    {loading ? '가입 중...' : '가입하기'}
                </button>
            </form>

            <div className="text-center mt-lg text-sm signup-footer">
                <span className="text-muted">이미 계정이 있으신가요? </span>
                <Link to="/login" className="text-primary hover:underline">
                    로그인
                </Link>
            </div>
        </div>
    );
}
