# PureBoost VN — Trình Tối Ưu Hóa Hệ Thống, Dọn Rác & Tăng Tốc Điện Thoại Chuyên Sâu

**PureBoost VN** là ứng dụng tối ưu hóa hiệu năng và dọn rác toàn diện cho Android, kết hợp khả năng giải phóng bộ nhớ đệm sâu, đóng tiến trình chạy ngầm gây hao pin, kiểm soát nhiệt độ chống nóng máy với 3 chế độ hiệu năng linh hoạt (**Tiết kiệm**, **Cân bằng**, **Max Mode**) trên nền giao diện tối giản **OLED Dark Emerald** siêu tiết kiệm pin và hỗ trợ tiếng Việt 100%.

---

### User Review & Critical Decisions

> [!IMPORTANT]
> Dưới đây là các quyết định thiết kế và tính năng cốt lõi đã được xác nhận từ câu trả lời của bạn để triển khai chính xác theo mong muốn:

- **Confirmed Decision 1 — Ba chế độ hiệu năng & kiểm soát nhiệt độ thủ công**: Tích hợp bộ chuyển đổi trực quan giữa 3 chế độ (**Tiết kiệm pin**, **Cân bằng**, **Max Mode**). Chế độ **Max Mode** ưu tiên giải phóng tối đa RAM khả dụng, thu hồi tiến trình chạy ngầm không cần thiết, kích hoạt `System.gc()` / thu gom bộ nhớ đệm và giám sát ngưỡng nhiệt độ pin thời gian thực để duy trì độ mượt tối đa mà không gây nóng máy.
- **Confirmed Decision 2 — Bộ 4 công cụ dọn rác & phân tích toàn diện**:
  1. Giải phóng RAM và đóng các tiến trình chạy ngầm thực tế thông qua `ActivityManager`.
  2. Quét và dọn dẹp bộ nhớ đệm (Cache), tệp rác tạm (`.tmp`, `.log`) và tệp tải xuống/APK thừa.
  3. Phân tích mức tiêu thụ pin, đo nhiệt độ thiết bị (`BatteryManager`) và cảnh báo ứng dụng gây nóng máy/hao pin.
  4. Kiểm tra và phân tích dung lượng lưu trữ chi tiết theo từng phân loại tệp (`StatFs` + phân loại Ứng dụng, Hình ảnh/Video, Tệp âm thanh, Tài liệu & Tệp rác).
- **Confirmed Decision 3 — Tự động dọn bộ nhớ đệm định kỳ cố định**: Cho phép bật/tắt và chọn chu kỳ tự động giải phóng bộ nhớ đệm định kỳ mỗi **1 giờ**, **6 giờ** hoặc **12 giờ** kèm thông báo tự động khi phát hiện ứng dụng chạy ngầm gây hao pin hoặc nhiệt độ vượt ngưỡng.
- **Confirmed Decision 4 — Phong cách thiết kế OLED Dark Emerald**: Sử dụng nền đen tuyền chuẩn OLED (`#050807` / `#0B1210`) kết hợp điểm nhấn Xanh Lục Bảo (`#10B981` / `#34D399`), vừa mang vẻ đẹp hiện đại, tinh gọn, vừa tắt điểm ảnh đen trên màn hình AMOLED/OLED để không tiêu tốn pin khi theo dõi biểu đồ thời gian thực.

---

### 1. Overview & Core Concept

- **What It Does**:
  - Cung cấp bảng điều khiển trung tâm theo dõi thời gian thực (RAM, Tải hệ thống/CPU ước tính, Nhiệt độ Pin, Dung lượng bộ nhớ) qua biểu đồ đường mượt mà không gây nặng máy.
  - Thực hiện quét sâu và dọn sạch tệp rác, bộ nhớ đệm, giải phóng RAM tức thì và hạ nhiệt thiết bị chỉ với một chạm.
  - Vận hành cơ chế tự động định kỳ (1h / 6h / 12h) và hệ thống cảnh báo thông minh qua Notification của Android khi phát hiện ứng dụng ngầm gây sụt pin hoặc tăng nhiệt.
- **Target Audience / Persona**: Người dùng Android tại Việt Nam muốn thiết bị luôn sạch sẽ như mới, hoạt động mượt mà ở cường độ cao (Max Mode) nhưng vẫn mát máy và tiết kiệm pin tối đa.
- **Key Value**: Tối ưu thực tế dựa trên các API hệ thống Android (`ActivityManager`, `BatteryManager`, `StatFs`, `PackageManager`, `NotificationManager`) kết hợp lưu trữ lịch sử tối ưu cục bộ an toàn, nhẹ nhàng và không tiêu hao tài nguyên nền.

---

### 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Bảng Điều Khiển & Biểu Đồ Thời Gian Thực (Trang chủ - "Tổng quan")**:
     - Hiển thị vòng cung năng lượng trung tâm thể hiện Điểm Sức Khỏe Hệ Thống (0–100%), % RAM đang dùng, nhiệt độ Pin (°C) và nút hành động chính **"Tối Ưu & Tăng Tốc Ngay"**.
     - Bộ chọn **3 Chế Độ Hiệu Năng** (*Tiết kiệm*, *Cân bằng*, *Max Mode*) với hiệu ứng chuyển trạng thái mượt mà: khi bật *Max Mode*, hệ thống tự động giải phóng các tiến trình nền, tối ưu bộ nhớ và kích hoạt lá chắn kiểm soát nhiệt.
     - **Biểu đồ theo dõi hiệu năng thời gian thực (Real-time Telemetry Chart)**: Biểu đồ Canvas 60fps siêu nhẹ vẽ trực tiếp biến thiên % RAM và Nhiệt độ (°C) theo từng giây với lưới tọa độ mảnh và vùng phủ gradient lục bảo.
  2. **Quét & Dọn Rác Chuyên Sâu (Tab "Dọn rác")**:
     - Hiển thị thanh phân bổ dung lượng lưu trữ chi tiết theo 5 danh mục: *Hệ thống*, *Ứng dụng & Dữ liệu*, *Đa phương tiện (Ảnh/Video/Nhạc)*, *Tài liệu & Tải xuống*, và *Bộ nhớ đệm / Tệp rác*.
     - Quét chi tiết từng nhóm rác: *Bộ nhớ đệm ứng dụng (Cache)*, *Tệp tạm & Nhật ký (.tmp/.log)*, *Tệp APK & Tải xuống thừa*, *Dữ liệu rác còn sót*. Người dùng có thể chọn từng mục và nhấn **"Dọn Sạch Ngay"** để giải phóng dung lượng.
  3. **Kiểm Soát Pin, Nhiệt Độ & Ứng Dụng Ngầm (Tab "Hạ nhiệt & Pin")**:
     - Hiển thị thông số pin chuyên sâu: Nhiệt độ hiện tại (°C), Điện áp (mV), Tình trạng sức khỏe pin, Trạng thái sạc và ước tính thời gian sử dụng theo từng chế độ (*Tiết kiệm / Cân bằng / Max Mode*).
     - Danh sách **Ứng dụng đang chạy / tiêu thụ tài nguyên nền** được trích xuất từ hệ thống kèm mức sử dụng bộ nhớ và nhãn cảnh báo *"Gây hao pin / Nóng máy"*. Cho phép tích chọn đóng đồng loạt (`killBackgroundProcesses`) hoặc mở trang thông tin hệ thống của ứng dụng để buộc dừng triệt để.
  4. **Lịch Tự Động & Cảnh Báo Thông Minh (Tab "Tự động")**:
     - Công tắc bật/tắt **Tự động giải phóng bộ nhớ đệm định kỳ** với 3 lựa chọn chu kỳ cố định: **Mỗi 1 giờ**, **Mỗi 6 giờ**, **Mỗi 12 giờ**, kèm nút *"Chạy thử chu kỳ tự động ngay"*.
     - Công tắc bật/tắt **Thông báo cảnh báo ứng dụng ngầm gây hao pin & quá nhiệt** (cho phép tùy chỉnh ngưỡng nhiệt độ cảnh báo, ví dụ 38°C – 42°C, và gửi thông báo thực tế lên thanh trạng thái Android).
     - Nhật ký lịch sử các lần dọn rác, tăng tốc và hạ nhiệt (lưu trữ trong cơ sở dữ liệu cục bộ Room).

- **Visual Identity & Theme**:
  - *Aesthetic Direction*: **OLED Dark Emerald Minimalist** — Tối giản kỹ thuật số, sắc nét, độ tương phản cao, tận dụng nền đen tuyền để tiết kiệm năng lượng màn hình.
  - *Color Palette & Mood*:
    - `--background`: `#050807` (Pure OLED Carbon Black)
    - `--surface`: `#0D1512` (Deep Forest Obsidian)
    - `--surface-variant`: `#14221D` (Elevated Emerald Slate)
    - `--primary`: `#10B981` (Vibrant Emerald — biểu trưng cho trạng thái mát, sạch và mượt)
    - `--secondary`: `#06B6D4` (Cyber Cyan — dùng cho chỉ số Max Mode và biểu đồ phụ)
    - `--warning` / `--error`: `#F59E0B` (Amber) & `#EF4444` (Crimson) — cảnh báo ứng dụng ngầm gây nóng máy hoặc RAM đầy.
  - *Typography & Hierarchy*: Kết hợp phông chữ tiêu đề & số liệu kỹ thuật hiện đại (**Space Grotesk** cho các con số thống kê dung lượng, nhiệt độ, % RAM) cùng phông chữ nội dung dễ đọc hỗ trợ tiếng Việt hoàn hảo (**Be Vietnam Pro**).
  - *Component Styling & Layout*: Bo góc chuẩn Material 3 (`16.dp` – `24.dp`), viền mảnh `1.dp` bán trong suốt (`#10B981` ở độ mờ 18%) tạo chiều sâu không cần đổ bóng nặng nề, hỗ trợ hiển thị thích ứng trên cả màn hình dọc và màn hình lớn.
- **Interactive Feedback & Motion**: Hiệu ứng radar/vòng sóng lan tỏa khi quét rác hoặc kích hoạt **Max Mode**, chuyển động lò xo (`spring()`) khi biểu đồ và thanh dung lượng cập nhật, cùng thông báo đẩy hệ thống (System Notification) trực quan.

---

### 3. Key Product Decisions & Trade-Offs

- **Decision 1 — Cơ chế Tăng tốc sâu (Boost) & Hạ nhiệt trong khuôn khổ bảo mật Android hiện đại**:
  - *Chosen Approach*: Kết hợp `ActivityManager.killBackgroundProcesses()` cho toàn bộ các gói ứng dụng chạy nền không thiết yếu, dọn dẹp thư mục bộ nhớ đệm/tệp tạm thực tế, thu hồi bộ nhớ VM, và cung cấp lối tắt mở nhanh cài đặt chi tiết ứng dụng (`ACTION_APPLICATION_DETAILS_SETTINGS`) đối với các ứng dụng cứng đầu cần buộc dừng sâu (Force Stop).
  - *Why*: Đảm bảo ứng dụng hoạt động ổn định, hợp lệ trên mọi thiết bị Android không cần quyền Root mà vẫn giải phóng RAM thực sự và giảm tải CPU/nhiệt độ tức thì.
  - *Alternatives Considered*: Yêu cầu quyền Root (bị loại bỏ vì không khả thi với đa số người dùng phổ thông và tiềm ẩn rủi ro bảo mật).
- **Decision 2 — Kiến trúc Biểu đồ Thời gian thực Không Gây Hao Pin**:
  - *Chosen Approach*: Vẽ biểu đồ trực tiếp trên Jetpack Compose `Canvas` với bộ đệm vòng (Circular Buffer 30 điểm dữ liệu gần nhất) và chỉ kích hoạt luồng lấy mẫu (Sampling Coroutine Flow) khi màn hình đang hiển thị (`Lifecycle.State.STARTED`).
  - *Why*: Giữ mức tiêu thụ CPU của chính ứng dụng PureBoost VN gần như bằng 0% khi thu nhỏ, đúng với tiêu chí *"không ăn pin nhưng mượt như Max Mode"*.
- **Decision 3 — Lưu trữ cấu hình tự động & Nhật ký tối ưu bằng Room Database**:
  - *Chosen Approach*: Sử dụng **Room Database** cục bộ để lưu lịch sử các phiên dọn rác/tăng tốc, dung lượng đã giải phóng và cấu hình chu kỳ tự động (1h / 6h / 12h).
  - *Why*: Dữ liệu chẩn đoán phần cứng và dọn rác gắn liền với từng thiết bị cụ thể, truy xuất cực nhanh dưới 1ms và hoạt động 100% ngoại tuyến không tốn pin đồng bộ mạng.

---

### 4. Technical Architecture & Data Strategy *(Technical Reference)*

- **Architecture & Component Diagram**:

```
┌───────────────────────────────────────────────────────────────────────────┐
│                        GIAO DIỆN JETPACK COMPOSE (UI)                     │
│  ┌─────────────────┐ ┌─────────────────┐ ┌──────────────┐ ┌────────────┐  │
│  │  Tab Tổng Quan  │ │   Tab Dọn Rác   │ │ Tab Hạ Nhiệt │ │Tab Tự Động │  │
│  │ • Vòng Sức Khỏe │ │ • Phân Loại Tệp │ │  & Quản Lý   │ │• Chu Kỳ    │  │
│  │ • 3 Chế Độ Boost│ │ • Quét 4 Nhóm   │ │  Ứng Dụng    │ │  1h/6h/12h │  │
│  │ • Biểu Đồ 60fps │ │   Rác & Cache   │ │  Chạy Ngầm   │ │• Nhật Ký   │  │
│  └────────┬────────┘ └────────┬────────┘ └──────┬───────┘ └─────┬──────┘  │
└───────────┼───────────────────┼─────────────────┼───────────────┼─────────┘
            │                   │                 │               │
            ▼                   ▼                 ▼               ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                   ZENBOOST VIEWMODEL (StateFlow / MVVM)                   │
│  • Quản lý chế độ hiệu năng (Tiết Kiệm / Cân Bằng / Max Mode)             │
│  • Luồng Telemetry thời gian thực (RAM %, CPU Load, Nhiệt độ °C, Pin)     │
│  • Điều phối bộ hẹn giờ tự động dọn đệm & phát hiện ứng dụng ngầm hao pin │
└───────────────────┬───────────────────────────────────┬───────────────────┘
                    │                                   │
                    ▼                                   ▼
┌───────────────────────────────────────┐ ┌─────────────────────────────────┐
│     SYSTEM OPTIMIZER ENGINE (SDK)     │ │   ROOM DATABASE & NOTIFICATION  │
│ • ActivityManager (RAM & Background)  │ │ • OptimizationLogDao (Lịch sử)  │
│ • BatteryManager (Nhiệt độ, Điện áp)  │ │ • ScheduleConfigDao (1h/6h/12h) │
│ • StatFs & File Scanner (Cache/Rác)   │ │ • BatteryAlertNotification      │
│ • PackageManager (Phân tích ứng dụng) │ │   (Cảnh báo ứng dụng hao pin)   │
└───────────────────────────────────────┘ └─────────────────────────────────┘
```

- **Data Model & State**:
  - `PerformanceMode`: Enum gồm `BATTERY_SAVER` (*Tiết kiệm*), `BALANCED` (*Cân bằng*), `MAX_MODE` (*Max Mode - Siêu mượt & Kiểm soát nhiệt*).
  - `TelemetrySnapshot`: Chứa dữ liệu thời gian thực gồm `timestamp`, `ramUsedMb`, `ramTotalMb`, `ramUsagePercent`, `cpuLoadPercent`, `batteryTempCelsius`, `batteryLevel`, `voltageMv`, `isCharging`, `healthScore`.
  - `StorageBreakdown`: Dung lượng chi tiết theo 5 phân loại (*Hệ thống*, *Ứng dụng*, *Đa phương tiện*, *Tài liệu/Tải xuống*, *Bộ nhớ đệm & Tệp rác*) tính từ `StatFs` và trình quét tệp.
  - `JunkCategoryItem`: Đại diện cho từng nhóm rác (*Bộ nhớ đệm ứng dụng*, *Tệp tạm & Nhật ký hệ thống*, *Tệp APK & Tải xuống thừa*, *Dữ liệu rác còn sót lại*) với danh sách tệp/gói con, kích thước và trạng thái chọn.
  - `BackgroundAppInfo`: Thông tin ứng dụng đang hoạt động ngầm (`packageName`, `appName`, `estimatedRamMb`, `batteryImpactLevel`, ` thermalRisk`, `isSelected`).
  - `OptimizationLogEntity` & `AutoScheduleSettingsEntity` (Room `@Entity`): Lưu trữ lịch sử dọn rác/tăng tốc (thời gian, loại tác vụ, số MB giải phóng, số ứng dụng đã đóng, nhiệt độ giảm) và cấu hình tự động định kỳ (bật/tắt, chu kỳ `1` / `6` / `12` giờ, ngưỡng nhiệt độ cảnh báo, bật/tắt thông báo).

- **Interactive Component & State Mapping**:
  - **Chuyển đổi 3 Chế độ (Tiết kiệm / Cân bằng / Max Mode)**: Khi người dùng chọn một chế độ, `PureBoostViewModel` cập nhật cấu hình hệ thống mục tiêu, thực thi ngay chu trình tối ưu tương ứng (ví dụ chọn **Max Mode** sẽ tự động thu hồi tiến trình chạy ngầm, giải phóng bộ nhớ đệm, khóa ngưỡng cảnh báo nhiệt độ ở mức an toàn và hiển thị bảng kết quả tối ưu).
  - **Nút "Tối Ưu & Tăng Tốc Ngay" (One-Tap Boost)**: Chạy hiệu ứng quét sâu đa tầng -> Đóng các ứng dụng nền qua `ActivityManager.killBackgroundProcesses` -> Xóa bộ nhớ đệm tạm -> Ghi nhận số MB RAM giải phóng vào `OptimizationLogDao` -> Cập nhật biểu đồ thời gian thực và Điểm Sức Khỏe.
  - **Quét & Dọn Rác theo Danh Mục**: Người dùng có thể bật/tắt từng danh mục rác, xem chi tiết dung lượng từng phần và nhấn **"Dọn Sạch"** để xóa tệp rác/cache thực tế và cập nhật ngay biểu đồ dung lượng lưu trữ.
  - **Quản lý Ứng dụng Ngầm & Hạ Nhiệt**: Cho phép lọc các ứng dụng có mức tiêu thụ pin cao, nhấn **"Đóng Ứng Dụng Ngầm"** để giải phóng toàn bộ ứng dụng đã chọn hoặc nhấn vào từng ứng dụng để mở màn hình Cài đặt hệ thống của ứng dụng đó.
  - **Hẹn giờ Tự động (1h / 6h / 12h) & Cảnh báo Notification**: Khi người dùng chọn chu kỳ (1 giờ, 6 giờ, hoặc 12 giờ) hoặc nhấn kiểm tra cảnh báo, hệ thống lưu cấu hình vào Room, cập nhật thời điểm chạy tiếp theo, và phát thông báo Android (`NotificationCompat`) ngay khi phát hiện ứng dụng ngầm gây hao pin/nóng máy (kèm quyền `POST_NOTIFICATIONS` chuẩn Android 13+).
