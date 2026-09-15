# توثيق Products API

## نظرة عامة

API بسيط لإدارة بيانات المنتجات (إضافة، جلب، تعديل، حذف، بحث) مع تخزين دائم في قاعدة بيانات.

| | |
|---|---|
| **Base URL** | `http://localhost:8080/api/products` |
| **صيغة البيانات** | JSON |
| **التوثيق التفاعلي (Swagger UI)** | `http://localhost:8080/swagger-ui.html` |
| **ملف OpenAPI (JSON)** | `http://localhost:8080/v3/api-docs` |
| **المصادقة** | لا يوجد حاليًا (مفتوح) |

> **ملحوظة:** بعد تشغيل المشروع (`mvn spring-boot:run`)، افتح رابط Swagger UI في المتصفح
> عشان تجرب كل الـ endpoints مباشرة من واجهة تفاعلية بدون كتابة أي كود.

---

## شكل بيانات المنتج (Product Object)

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

| الحقل | النوع | إلزامي | الوصف |
|---|---|---|---|
| `id` | Long | تلقائي | رقم المنتج (يتولد تلقائيًا، لا يُرسل عند الإنشاء) |
| `name` | String | ✅ نعم | اسم المنتج (لا يمكن أن يكون فارغًا) |
| `description` | String | ❌ لا | وصف المنتج |
| `price` | Double | ✅ نعم | السعر (يجب أن يكون رقمًا موجبًا) |
| `quantity` | Integer | ❌ لا | الكمية المتاحة (القيمة الافتراضية: 0) |
| `createdAt` | DateTime | تلقائي | تاريخ الإنشاء |
| `updatedAt` | DateTime | تلقائي | تاريخ آخر تعديل |

---

## Endpoints

### 1. جلب كل المنتجات

```
GET /api/products
```

**مثال طلب:**
```bash
curl http://localhost:8080/api/products
```

**الرد (200 OK):**
```json
[
  {
    "id": 1,
    "name": "لابتوب",
    "description": "لابتوب للألعاب",
    "price": 15000.0,
    "quantity": 10,
    "createdAt": "2026-09-08T10:00:00",
    "updatedAt": "2026-09-08T10:00:00"
  }
]
```

---

### 2. جلب منتج واحد

```
GET /api/products/{id}
```

**Path Parameters:**
| الاسم | النوع | الوصف |
|---|---|---|
| `id` | Long | رقم المنتج |

**مثال طلب:**
```bash
curl http://localhost:8080/api/products/1
```

**الرد (200 OK):**
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

**الرد عند عدم وجود المنتج (404 Not Found):**
```json
{
  "timestamp": "2026-09-08T10:05:00",
  "status": 404,
  "error": "Not Found",
  "message": "المنتج برقم 1 غير موجود"
}
```

---

### 3. البحث عن منتجات بالاسم

```
GET /api/products/search?name={query}
```

**Query Parameters:**
| الاسم | النوع | إلزامي | الوصف |
|---|---|---|---|
| `name` | String | ✅ نعم | جزء من الاسم (بحث غير حساس لحالة الأحرف) |

**مثال طلب:**
```bash
curl "http://localhost:8080/api/products/search?name=لابتوب"
```

**الرد (200 OK):**
```json
[
  {
    "id": 1,
    "name": "لابتوب",
    "description": "لابتوب للألعاب",
    "price": 15000.0,
    "quantity": 10,
    "createdAt": "2026-09-08T10:00:00",
    "updatedAt": "2026-09-08T10:00:00"
  }
]
```

---

### 4. إضافة منتج جديد

```
POST /api/products
```

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "name": "لابتوب",
  "description": "لابتوب للألعاب",
  "price": 15000,
  "quantity": 10
}
```

**مثال طلب:**
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

**الرد (201 Created):**
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

**الرد عند بيانات غير صحيحة (400 Bad Request):**
```json
{
  "timestamp": "2026-09-08T10:05:00",
  "status": 400,
  "error": "Validation Failed",
  "messages": {
    "name": "اسم المنتج مطلوب",
    "price": "السعر لازم يكون رقم موجب"
  }
}
```

---

### 5. تحديث منتج موجود

```
PUT /api/products/{id}
```

**Path Parameters:**
| الاسم | النوع | الوصف |
|---|---|---|
| `id` | Long | رقم المنتج المراد تعديله |

**Body:** (نفس شكل بيانات الإنشاء)
```json
{
  "name": "لابتوب Pro",
  "description": "نسخة محدّثة",
  "price": 18000,
  "quantity": 5
}
```

**مثال طلب:**
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "لابتوب Pro",
    "description": "نسخة محدّثة",
    "price": 18000,
    "quantity": 5
  }'
```

**الرد (200 OK):**
```json
{
  "id": 1,
  "name": "لابتوب Pro",
  "description": "نسخة محدّثة",
  "price": 18000.0,
  "quantity": 5,
  "createdAt": "2026-09-08T10:00:00",
  "updatedAt": "2026-09-08T10:20:00"
}
```

**الرد عند عدم وجود المنتج (404 Not Found):** نفس شكل الخطأ في endpoint رقم 2.

---

### 6. حذف منتج

```
DELETE /api/products/{id}
```

**Path Parameters:**
| الاسم | النوع | الوصف |
|---|---|---|
| `id` | Long | رقم المنتج المراد حذفه |

**مثال طلب:**
```bash
curl -X DELETE http://localhost:8080/api/products/1
```

**الرد:** `204 No Content` (بدون Body)

**الرد عند عدم وجود المنتج (404 Not Found):** نفس شكل الخطأ في endpoint رقم 2.

---

## أكواد الحالة (Status Codes)

| الكود | المعنى |
|---|---|
| `200 OK` | الطلب نجح (GET, PUT) |
| `201 Created` | تم إنشاء المورد بنجاح (POST) |
| `204 No Content` | تم الحذف بنجاح (DELETE) |
| `400 Bad Request` | بيانات مُرسلة غير صحيحة (فشل validation) |
| `404 Not Found` | المورد غير موجود |
| `500 Internal Server Error` | خطأ غير متوقع في السيرفر |

---

## شكل رسائل الأخطاء الموحد

كل الأخطاء بترجع بنفس الشكل العام:

```json
{
  "timestamp": "2026-09-08T10:05:00",
  "status": 404,
  "error": "Not Found",
  "message": "وصف الخطأ"
}
```

عدا أخطاء الـ validation اللي بترجع تفاصيل لكل حقل:

```json
{
  "timestamp": "2026-09-08T10:05:00",
  "status": 400,
  "error": "Validation Failed",
  "messages": {
    "name": "اسم المنتج مطلوب"
  }
}
```

---

## ملخص سريع (Quick Reference)

| Method | Endpoint | الوصف |
|---|---|---|
| `GET` | `/api/products` | جلب كل المنتجات |
| `GET` | `/api/products/{id}` | جلب منتج واحد |
| `GET` | `/api/products/search?name=` | البحث بالاسم |
| `POST` | `/api/products` | إضافة منتج |
| `PUT` | `/api/products/{id}` | تحديث منتج |
| `DELETE` | `/api/products/{id}` | حذف منتج |
