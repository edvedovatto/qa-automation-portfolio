.PHONY: test app ui api stop

test: app ui api

app:
	docker compose up -d
	timeout 120 sh -c 'until curl -sf http://localhost:3000 > /dev/null; do sleep 2; done'

ui:
	cd e2e-playwright && npm ci && npx playwright install chromium && npm test

api:
	cd api-restassured && mvn -B test

stop:
	docker compose down
