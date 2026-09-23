# Spring Boot REST API - مثال تخزين منتجات

**Powered and developed by: Ibrahim khamiss — +201014778296**

مشروع كامل بيستقبل بيانات عبر REST API ويخزنها في قاعدة بيانات (H2 مبدئيًا، وتقدر تبدلها بـ MySQL بسهولة).

## المتطلبات
- Java 17 أو أحدث
- Maven 3.6+ (أو استخدم `mvnw` لو حابب تجنب تنصيبه)
- **SQL Server** يعمل ومتاح (محليًا أو على سيرفر)، مع قاعدة بيانات جاهزة

## إعداد الاتصال بـ SQL Server

افتح ملف `src/main/resources/application.properties` وعدّل القيم دي حسب بياناتك:

```properties
spring.datasource.url=jdbc:sqlserver://SERVER_NAME:1433;databaseName=DATABASE_NAME;encrypt=true;trustServerCertificate=true
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

| القيمة | المقصود بيها |
|---|---|
| `SERVER_NAME` | عنوان السيرفر، مثلاً `localhost` أو `192.168.1.10` أو `myserver.database.windows.net` (Azure) |
| `1433` | البورت الافتراضي لـ SQL Server (غيّره لو مختلف عندك) |
| `DATABASE_NAME` | اسم قاعدة البيانات (لازم تكون منشأة مسبقًا في SQL Server) |
| `YOUR_USERNAME` / `YOUR_PASSWORD` | بيانات دخول SQL Server (SQL Authentication) |

> **مهم:** قاعدة البيانات نفسها (`DATABASE_NAME`) لازم تكون موجودة فعلاً في SQL Server قبل التشغيل
> (SQL Server، على عكس H2، مش بينشئ قاعدة البيانات تلقائيًا). تقدر تنشئها بأمر بسيط:
> ```sql
> CREATE DATABASE DATABASE_NAME;
> ```
> أما الجداول جوه القاعدة فبتتنشأ تلقائيًا (`spring.jpa.hibernate.ddl-auto=update`).

## طريقة التشغيل

```bash
cd springboot-api
mvn spring-boot:run
```

السيرفر هيشتغل على: `http://localhost:8080`

## التوثيق

- **توثيق تفاعلي (Swagger UI):** بعد تشغيل المشروع، افتح `http://localhost:8080/swagger-ui.html`
  لتجربة كل الـ endpoints مباشرة من المتصفح.
- **توثيق مكتوب كامل:** راجع ملف [`API_DOCUMENTATION.md`](./API_DOCUMENTATION.md) لكل التفاصيل
  (شكل البيانات، أمثلة الطلبات والردود، أكواد الأخطاء).

## الـ Endpoints المتاحة

| Method | Endpoint | الوظيفة |
|--------|----------|---------|
| GET    | `/api/products` | جلب كل المنتجات |
| GET    | `/api/products/{id}` | جلب منتج واحد |
| GET    | `/api/products/search?name=xxx` | البحث بالاسم |
| POST   | `/api/products` | إضافة منتج جديد |
| PUT    | `/api/products/{id}` | تحديث منتج |
| DELETE | `/api/products/{id}` | حذف منتج |

## مثال لإرسال بيانات (POST)

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "لابتوب",
    "description": "لابتوب للألعاب",
    "price": 15000,
    "quantity": 10
  }'
```

الرد المتوقع:
```json
{
  "id": 1,
  "name": "لابتوب",
  "description": "لابتوب للألعاب",
  "price": 15000.0,
  "quantity": 10,
  "createdAt": "2026-09-08T10:00:00",
  "updatedAt": "2026-09-08T10:00:00"
}
```

## مشاهدة قاعدة البيانات

بما إن المشروع بقى يستخدم SQL Server، تقدر تشوف الجداول والبيانات من خلال:
- **SQL Server Management Studio (SSMS)** — اتصل بنفس السيرفر وقاعدة البيانات المكتوبين في `application.properties`
- **Azure Data Studio** — بديل خفيف لـ SSMS

بعد أول تشغيل للمشروع هتلاقي جدول `products` اتنشأ تلقائيًا جوه قاعدة البيانات.

## التبديل لقاعدة بيانات تانية (H2 أو MySQL)

ملف `application.properties` فيه أقسام معلّقة (`#`) لكل من H2 و MySQL جاهزة للاستخدام —
بس امسح التعليق عن القسم اللي محتاجه وعلّق على قسم SQL Server.

## بنية المشروع

```
src/main/java/com/example/api/
├── ApiApplication.java          # نقطة التشغيل
├── entity/Product.java          # شكل الجدول في قاعدة البيانات
├── repository/ProductRepository.java  # التعامل مع قاعدة البيانات
├── service/ProductService.java  # منطق العمل
├── controller/ProductController.java  # استقبال طلبات الـ API
└── exception/                   # معالجة الأخطاء
```

## عايز تعدّل البيانات المخزّنة؟

بس غيّر الحقول في `Product.java` (مثلاً لو عايز تخزن "عملاء" بدل "منتجات":
name, email, phone... إلخ)، والباقي (Repository, Service, Controller) هيشتغل
تلقائيًا مع الحقول الجديدة لأنه مبني بشكل عام.
