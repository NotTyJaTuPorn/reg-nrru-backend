-- =============================================
-- ระบบลงทะเบียนเรียนออนไลน์ NRRU (Mini-Project)
-- Database: PostgreSQL
-- =============================================

-- 1. คณะ
CREATE TABLE faculties (
                           faculty_id BIGSERIAL PRIMARY KEY,
                           faculty_code VARCHAR(50) UNIQUE NOT NULL,
                           faculty_name_th VARCHAR(255) NOT NULL,
                           faculty_name_en VARCHAR(255),
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. ภาควิชา
CREATE TABLE departments (
                             department_id BIGSERIAL PRIMARY KEY,
                             faculty_id BIGINT NOT NULL REFERENCES faculties(faculty_id) ON DELETE CASCADE,
                             department_code VARCHAR(50) UNIQUE NOT NULL,
                             department_name_th VARCHAR(255) NOT NULL,
                             department_name_en VARCHAR(255),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. ผู้ใช้ระบบ (รวมนักศึกษา/อาจารย์/Admin)
CREATE TABLE users (
                       user_id BIGSERIAL PRIMARY KEY,
                       login_id VARCHAR(100) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       email VARCHAR(150) UNIQUE NOT NULL,
                       title_th VARCHAR(20),
                       first_name_th VARCHAR(100) NOT NULL,
                       last_name_th VARCHAR(100) NOT NULL,
                       title_en VARCHAR(20),
                       first_name_en VARCHAR(100),
                       last_name_en VARCHAR(100),
                       phone_number VARCHAR(20),
                       role_name VARCHAR(20) NOT NULL CHECK (role_name IN ('STUDENT', 'LECTURER', 'ADMIN')),
                       pdpa_consent_flag BOOLEAN DEFAULT FALSE,
                       pdpa_consent_datetime TIMESTAMP,
                       is_active_flag BOOLEAN DEFAULT TRUE,
                       last_login_datetime TIMESTAMP,
                       created_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. นักศึกษา
CREATE TABLE students (
                          student_id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT UNIQUE NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                          student_code VARCHAR(20) UNIQUE NOT NULL,
                          current_year INT NOT NULL,
                          department_id BIGINT NOT NULL REFERENCES departments(department_id),
                          advisor_user_id BIGINT REFERENCES users(user_id),
                          cumulative_gpa DECIMAL(3,2) DEFAULT 0.00,
                          accumulated_credits INT DEFAULT 0,
                          tuition_paid BOOLEAN DEFAULT FALSE,
                          is_registration_confirmed BOOLEAN DEFAULT FALSE,
                          created_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. อาจารย์
CREATE TABLE lecturers (
                           lecturer_id BIGSERIAL PRIMARY KEY,
                           user_id BIGINT UNIQUE NOT NULL REFERENCES users(user_id) ON DELETE CASCADE,
                           lecturer_code VARCHAR(20) UNIQUE NOT NULL,
                           academic_rank VARCHAR(50),
                           department_id BIGINT NOT NULL REFERENCES departments(department_id),
                           created_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 6. รายวิชา (สำคัญ: มี seat_capacity และ enrolled_student_count)
CREATE TABLE courses (
                         course_id BIGSERIAL PRIMARY KEY,
                         course_code VARCHAR(20) UNIQUE NOT NULL,
                         course_name_th VARCHAR(255) NOT NULL,
                         course_name_en VARCHAR(255),
                         credit_count INT NOT NULL CHECK (credit_count > 0),
                         seat_capacity INT NOT NULL,
                         enrolled_student_count INT DEFAULT 0 CHECK (enrolled_student_count <= seat_capacity),
                         lecturer_user_id BIGINT REFERENCES users(user_id),
                         faculty_id BIGINT NOT NULL REFERENCES faculties(faculty_id),
                         semester_code VARCHAR(10) NOT NULL,
                         academic_year INT NOT NULL,
                         course_description TEXT,
                         is_open_for_registration BOOLEAN DEFAULT TRUE,
                         created_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         updated_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. วิชาบังคับก่อน
CREATE TABLE course_prerequisites (
                                      prerequisite_id BIGSERIAL PRIMARY KEY,
                                      course_id BIGINT NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
                                      prerequisite_course_id BIGINT NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
                                      UNIQUE(course_id, prerequisite_course_id)
);

-- 8. ตารางสอน
CREATE TABLE schedules (
                           schedule_id BIGSERIAL PRIMARY KEY,
                           course_id BIGINT NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
                           weekday_code VARCHAR(3) NOT NULL,
                           start_time TIME NOT NULL,
                           end_time TIME NOT NULL,
                           room_name VARCHAR(100),
                           UNIQUE(weekday_code, start_time, end_time, room_name)
);

-- 9. การลงทะเบียน (หัวใจหลัก!)
CREATE TABLE enrollments (
                             enrollment_id BIGSERIAL PRIMARY KEY,
                             student_id BIGINT NOT NULL REFERENCES students(student_id) ON DELETE CASCADE,
                             course_id BIGINT NOT NULL REFERENCES courses(course_id) ON DELETE CASCADE,
                             enrollment_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             enrollment_status VARCHAR(20) DEFAULT 'REGISTERED' CHECK (enrollment_status IN ('REGISTERED', 'DROPPED', 'WAITLIST')),
                             final_grade VARCHAR(2),
                             UNIQUE(student_id, course_id),
                             created_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 10. ช่วงเวลาลงทะเบียน
CREATE TABLE registration_slots (
                                    slot_id BIGSERIAL PRIMARY KEY,
                                    semester_code VARCHAR(10) NOT NULL,
                                    academic_year INT NOT NULL,
                                    target_year INT,
                                    slot_type VARCHAR(20) DEFAULT 'REGULAR',
                                    slot_start_datetime TIMESTAMP NOT NULL,
                                    slot_end_datetime TIMESTAMP NOT NULL,
                                    is_active_flag BOOLEAN DEFAULT TRUE,
                                    created_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 11. Audit Logs (PDPA)
CREATE TABLE audit_logs (
                            log_id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT REFERENCES users(user_id) ON DELETE SET NULL,
                            action_type VARCHAR(100) NOT NULL,
                            action_details JSONB,
                            created_datetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- INDEXES (เพื่อความเร็ว)
-- =============================================
CREATE INDEX idx_courses_semester ON courses(semester_code, academic_year);
CREATE INDEX idx_courses_lecturer ON courses(lecturer_user_id);
CREATE INDEX idx_enrollments_student ON enrollments(student_id);
CREATE INDEX idx_enrollments_course ON enrollments(course_id);
CREATE INDEX idx_enrollments_status ON enrollments(enrollment_status);
CREATE INDEX idx_schedules_course ON schedules(course_id);
CREATE INDEX idx_users_role ON users(role_name);
CREATE INDEX idx_students_advisor ON students(advisor_user_id);
CREATE INDEX idx_registration_slots_time ON registration_slots(slot_start_datetime, slot_end_datetime);
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action_type);