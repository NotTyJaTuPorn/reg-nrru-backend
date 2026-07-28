-- Seed Faculties
INSERT INTO faculties (faculty_code, faculty_name_th, faculty_name_en) VALUES
('01', 'คณะวิทยาศาสตร์และเทคโนโลยี', 'Faculty of Science and Technology'),
('02', 'คณะครุศาสตร์', 'Faculty of Education')
ON CONFLICT (faculty_code) DO NOTHING;

-- Seed Departments
INSERT INTO departments (faculty_id, department_code, department_name_th, department_name_en) VALUES
(1, '0101', 'วิทยาการคอมพิวเตอร์', 'Computer Science'),
(1, '0102', 'เทคโนโลยีสารสนเทศ', 'Information Technology'),
(2, '0201', 'คอมพิวเตอร์ศึกษา', 'Computer Education')
ON CONFLICT (department_code) DO NOTHING;

-- Seed Users (Password is 'password123' for all)
-- Hash: $2a$10$iRClFVrx3rvbm9wXnzH50.7rN7z4E41LjNc3HfzO6GmeApQVPP33S
INSERT INTO users (login_id, password_hash, email, title_th, first_name_th, last_name_th, title_en, first_name_en, last_name_en, phone_number, role_name, pdpa_consent_flag, is_active_flag) VALUES
('admin01', '$2a$10$iRClFVrx3rvbm9wXnzH50.7rN7z4E41LjNc3HfzO6GmeApQVPP33S', 'admin01@nrru.ac.th', 'นาย', 'สมชาย', 'ใจดี', 'Mr.', 'Somchai', 'Jaidee', '0812345678', 'ADMIN', true, true),
('6600001', '$2a$10$iRClFVrx3rvbm9wXnzH50.7rN7z4E41LjNc3HfzO6GmeApQVPP33S', 'student01@nrru.ac.th', 'นางสาว', 'สมศรี', 'มีสุข', 'Miss', 'Somsri', 'Meesook', '0898765432', 'STUDENT', true, true),
('lecturer01', '$2a$10$iRClFVrx3rvbm9wXnzH50.7rN7z4E41LjNc3HfzO6GmeApQVPP33S', 'lecturer01@nrru.ac.th', 'ดร.', 'วิชาญ', 'เรียนดี', 'Dr.', 'Wichan', 'Riandee', '0855555555', 'LECTURER', true, true)
ON CONFLICT (login_id) DO NOTHING;

-- Seed Students
INSERT INTO students (user_id, student_code, current_year, department_id, cumulative_gpa, accumulated_credits) VALUES
(2, '6600001', 2, 1, 3.75, 45)
ON CONFLICT (student_code) DO NOTHING;

-- Seed Lecturers
INSERT INTO lecturers (user_id, lecturer_code, academic_rank, department_id) VALUES
(3, 'LEC001', 'อาจารย์', 1)
ON CONFLICT (lecturer_code) DO NOTHING;

-- Seed Courses
INSERT INTO courses (course_code, course_name_th, course_name_en, credit_count, seat_capacity, enrolled_student_count, lecturer_user_id, faculty_id, semester_code, academic_year, course_description, is_open_for_registration) VALUES
('CS101', 'การเขียนโปรแกรมคอมพิวเตอร์ 1', 'Computer Programming I', 3, 40, 0, 3, 1, '1', 2026, 'หลักการเขียนโปรแกรมคอมพิวเตอร์เบื้องต้น โครงสร้างควบคุม และการเขียนโปรแกรมเชิงโครงสร้าง', true),
('CS102', 'โครงสร้างข้อมูลและอัลกอริทึม', 'Data Structures and Algorithms', 3, 30, 0, 3, 1, '1', 2026, 'แนวคิดโครงสร้างข้อมูลเชิงเส้นและไม่เชิงเส้น การค้นหาและการจัดเรียงข้อมูล', true)
ON CONFLICT (course_code) DO NOTHING;
