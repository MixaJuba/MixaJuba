FROM python:3.11-slim
WORKDIR /app
COPY requirements.txt ./
RUN pip install --no-cache-dir -r requirements.txt
COPY diia_scraper/ diia_scraper/
COPY . .
CMD ["python", "-m", "diia_scraper.cli"]
