.PHONY: build run

build:
	docker build --tag speechpal-server:mac .

run:
	docker run -p 8080:8080 speechpal-server:mac

build-cloud:
	gcloud builds submit --region=us-west2 --tag us-west2-docker.pkg.dev/horizontal-leaf-386604/speechpal-repo/speechpal-server

run-cloud:
	gcloud run deploy speechpal-server --image us-west2-docker.pkg.dev/horizontal-leaf-386604/speechpal-repo/speechpal-server
