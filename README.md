# NRRU Online Registration System (Backend)

ระบบบริหารจัดการและลงทะเบียนเรียนออนไลน์ของมหาวิทยาลัยราชภัฏนครราชสีมา (จำลอง) ในส่วนของหลังบ้าน (Backend REST API) พัฒนาขึ้นโดยใช้เทคโนโลยี Spring Boot 3

> **หมายเหตุ:** โปรเจกต์นี้ได้รับการพัฒนาขึ้นจากกรณีศึกษาเพื่อวิเคราะห์และแก้ไขปัญหาซอฟต์แวร์บริการการศึกษา ในรายวิชา Software Project Management (SPM) โดยเป็นโปรเจกต์ส่วนบุคคลที่พัฒนาขึ้นเพื่อการเรียนรู้การเขียนโปรแกรมและการวิเคราะห์ระบบเท่านั้น ไม่มีความเกี่ยวข้องโดยตรงกับมหาวิทยาลัย

---

## 🛠️ เทคโนโลยีที่เลือกใช้ (Tech Stack)
*   **ภาษาหลัก:** Java 25
*   **เฟรมเวิร์ก:** Spring Boot 4.0.7
*   **การจัดการฐานข้อมูล (ORM):** Spring Data JPA (Hibernate)
*   **ระบบความปลอดภัย:** Spring Security + Auth0 JWT (JSON Web Token)
*   **ฐานข้อมูล:** PostgreSQL
*   **การควบคุมเวอร์ชันฐานข้อมูล:** Flyway Database Migration
*   **เครื่องมืออื่นๆ:** Lombok

---

## 📋 ฟีเจอร์หลักของระบบ (Features)
1.  **ระบบพิสูจน์ตัวตน (Authentication):** รองรับระบบลงชื่อเข้าใช้งานและการออก JWT Token ที่แบ่งกลุ่มสิทธิ์ออกเป็น 3 ระดับอย่างชัดเจน ได้แก่ `STUDENT`, `LECTURER`, และ `ADMIN`
2.  **ฟังก์ชันสำหรับนักศึกษา (Student Portal):**
    *   ดูตารางเรียน/ตารางสอบ
    *   ลงทะเบียนเรียน เพิ่ม-ลดรายวิชา
    *   ยืนยันผลการลงทะเบียนเรียน และคำนวณยอดชำระเงินค่าเล่าเรียน
    *   ตรวจสอบประวัติผลการเรียน (เกรด) และข้อมูลโปรไฟล์ส่วนตัว
3.  **ฟังก์ชันสำหรับอาจารย์ (Lecturer Portal):**
    *   ดึงรายชื่อและรายละเอียดของนักศึกษาในที่ปรึกษา (Advisee List)
    *   ตรวจสอบตารางเรียน/การลงทะเบียนของนักศึกษาในที่ปรึกษา
    *   ดึงข้อมูลวิชาที่รับผิดชอบสอน
    *   บันทึกและส่งเกรดให้กับนักศึกษาในแต่ละรายวิชาที่สอน
4.  **ฟังก์ชันสำหรับผู้ดูแลระบบ (Admin Portal):**
    *   ระบบจัดการผู้ใช้งาน (เพิ่ม แก้ไข ลบ ข้อมูลนักศึกษาและอาจารย์)
    *   ระบบจัดการรายวิชาและเปิดปิดส่วนการลงทะเบียน
    *   ระบบตั้งค่าภาคเรียนและการเปิด-ปิดช่วงเวลาลงทะเบียนชั่วคราว

---

## 🚀 วิธีการเริ่มต้นใช้งานในเครื่องของคุณ (Getting Started)

### 1. ความต้องการของระบบ (Prerequisites)
*   Java Development Kit (JDK) 17 หรือใหม่กว่า
*   PostgreSQL Database
*   เครื่องมือ build อย่าง Gradle (ติดตั้งมาพร้อมกับ Gradle Wrapper ในโครงการแล้ว)

### 2. การเตรียมฐานข้อมูล (Database Setup)
1.  เข้าฐานข้อมูล PostgreSQL และสร้าง Database ใหม่ขึ้นมาชื่อ `registration_db`:
    ```sql
    CREATE DATABASE registration_db;
    ```
2.  เปิดไฟล์คอนฟิกหลัก [application.yml](src/main/resources/application.yml) แล้วแก้ไขชื่อผู้ใช้และรหัสผ่านเชื่อมต่อฐานข้อมูลตามเครื่องคอมพิวเตอร์ของคุณ:
    ```yaml
    spring:
      datasource:
        url: jdbc:postgresql://localhost:5432/registration_db
        username: your_postgresql_username
        password: your_postgresql_password
    ```

### 3. การรัน Migration และ Mock Data
เมื่อคุณรันแอปพลิเคชันเป็นครั้งแรก ระบบจะทำการสร้างตารางฐานข้อมูลและใส่ข้อมูลจำลองให้อัตโนมัติ (ผ่านระบบ Flyway Migration ในโฟลเดอร์ `src/main/resources/db/migration/`)
*   `V1__init_schema.sql` สร้างโครงสร้าง Table ต่างๆ
*   `V2__insert_mock_data.sql` ใส่ข้อมูลบัญชีจำลองผู้ดูแลระบบ, นักศึกษา, อาจารย์ และรายวิชา

### 4. คำสั่งในการรัน Backend
รันเซิร์ฟเวอร์ด้วย Gradle Wrapper บน Terminal:
*   **สำหรับ Windows PowerShell:**
    ```powershell
    ./gradlew.bat bootRun
    ```
*   **สำหรับ macOS / Linux:**
    ```bash
    ./gradlew bootRun
    ```

เซิร์ฟเวอร์จะเปิดใช้งาน REST API ที่พอร์ต `http://localhost:8080`

### 5. การรัน Unit Tests
ตรวจสอบความถูกต้องของสิทธิ์ ความปลอดภัย และตรรกะในส่วนของการลงทะเบียนเรียน concurrent-safety ด้วย JUnit 5:
```bash
./gradlew.bat test
```

---

## 🏛️ โครงสร้างของโปรเจกต์ (Project Directory Structure)
```text
reg-nrru-backend/
├── build.gradle               # สคริปต์ตั้งค่าไลบรารีและการ Build ของ Gradle
├── src/
│   ├── main/
│   │   ├── java/com/nrru/registration/
│   │   │   ├── config/        # การตั้งค่าความปลอดภัย (Security), JWT และ CORS
│   │   │   ├── controller/    # ตัวรับ Endpoint REST API (Admin, Auth, Course, Enrollment, Lecturer, Student)
│   │   │   ├── dto/           # Data Transfer Objects สำหรับรับส่งข้อมูล
│   │   │   ├── entity/        # JPA Entities สำหรับ Mapping กับตารางฐานข้อมูล
│   │   │   ├── repository/    # JPA Repositories สำหรับคุยกับฐานข้อมูล SQL
│   │   │   ├── service/       # Business Logic การคำนวณและตรวจสอบเงื่อนไขต่างๆ
│   │   │   └── RegNrruBackendApplication.java  # คลาสหลักในการเปิดแอปพลิเคชัน
│   │   └── resources/
│   │       ├── application.yml                 # ตั้งค่าระบบการเชื่อมต่อฐานข้อมูลและพอร์ตเซิร์ฟเวอร์
│   │       └── db/migration/                   # สคริปต์ SQL ของ Flyway Migration
│   └── test/                  # โฟลเดอร์สำหรับการเขียน Unit / Integration Testing
```

---

## 📄 สิทธิ์การใช้งาน (License)
โครงการนี้เผยแพร่ภายใต้ใบอนุญาต **[MIT License](LICENSE)** - สามารถนำไปใช้งาน ปรับปรุง แก้ไขต่อยอดเพื่อการศึกษาได้อย่างอิสระ
