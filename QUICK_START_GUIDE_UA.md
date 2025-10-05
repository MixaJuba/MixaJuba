# 🔥 ШВИДКИЙ СТАРТ | QuantumForce_Code UA

## ⚡️ Встановлення за 5 хвилин

### 1. Клонування репозиторію:
```bash
git clone https://github.com/MixaJuba/QuantumForce_Code.git
cd QuantumForce_Code
```

### 2. Встановлення залежностей:
```bash
pip install -r requirements.txt
# або
npm install  # якщо Node.js версія
```

### 3. Перша діагностика:
```python
from quantum_force import AutoDiagnostic

# Ініціалізація AI діагноста
diagnostic = AutoDiagnostic()

# Підключення до OBD-II
diagnostic.connect_obd(port="/dev/ttyUSB0")

# Запуск діагностики
result = diagnostic.run_full_scan()
print(f"Діагноз: {result.diagnosis}")
print(f"Впевненість: {result.confidence}%")
```

## 🚗 Швидке тестування

### Тест без авто (емуляція):
```python
# Емуляція OBD даних
test_data = {
    "rpm": 2500,
    "speed": 80,
    "coolant_temp": 90,
    "fuel_pressure": 3.2
}

result = diagnostic.simulate_diagnosis(test_data)
print("Результат тесту:", result)
```

### Тест з реальним авто:
1. Підключи OBD-II адаптер до автомобіля
2. Запусти двигун
3. Виконай команду: `python quick_test.py`

## 🛠️ Налаштування для вашого СТО

### Крок 1: Налаштування бази даних
```python
# config.py
DATABASE_CONFIG = {
    "host": "localhost",
    "database": "auto_diagnostics",
    "user": "sto_admin",
    "password": "your_secure_password"
}
```

### Крок 2: Інтеграція з касовою системою
```python
# pos_integration.py
from quantum_force import POSIntegration

pos = POSIntegration()
pos.configure_printer(ip="192.168.1.100")
pos.configure_payment(terminal_id="STO_001")
```

### Крок 3: Налаштування звітності
```python
# reports.py
diagnostic.generate_daily_report(date="2025-10-05")
diagnostic.export_statistics(format="pdf")
```

## 🚨 Швидке вирішення проблем

### Проблема: OBD не підключається
**Рішення:**
```bash
# Перевір порти
ls /dev/tty*
# Встанови драйвера
sudo apt-get install obd-tools
```

### Проблема: Низька точність діагностики
**Рішення:**
```python
# Оновлення моделі AI
diagnostic.update_ai_model()
diagnostic.retrain_on_local_data("your_data.csv")
```

### Проблема: Повільна робота
**Рішення:**
```python
# Оптимізація продуктивності
diagnostic.enable_gpu_acceleration()
diagnostic.set_cache_size(1024)  # MB
```

## 📱 Мобільний додаток

### Встановлення:
1. Скачай APK з [releases](https://github.com/MixaJuba/QuantumForce_Code/releases)
2. Встанови на Android пристрій
3. Підключись до WiFi мережі СТО

### Використання:
```
1. Відскануй QR-код автомобіля
2. Підключи OBD адаптер
3. Натисни "СТАРТ ДІАГНОСТИКИ"
4. Очікуй результат (30-60 сек)
```

## 💡 Поради для початківців

### ✅ Що робити:
- Завжди прогрівай двигун перед діагностикою
- Використовуй оригінальні OBD-II адаптери
- Регулярно оновлюй AI моделі
- Веди статистику для покращення точності

### ❌ Чого не робити:
- Не діагностуй на холодному двигуні
- Не використовуй дешеві китайські адаптери
- Не ігноруй попередження системи
- Не забувай про резервне копіювання даних

## 🔧 Команди для експертів

```bash
# Глибока діагностика
python expert_mode.py --deep-scan --all-modules

# Експорт лог-файлів
python export_logs.py --format json --period 7days

# Калібрування сенсорів
python calibrate.py --sensor-type oxygen --auto-adjust

# Генерація звіту для страхової
python insurance_report.py --case-id 12345 --format official
```

---

**🇺🇦 Зроблено в Україні для українських автомайстрів!**  
**⚡️ Будь першим, хто володіє майбутнім автодіагностики! 🚦🤖**