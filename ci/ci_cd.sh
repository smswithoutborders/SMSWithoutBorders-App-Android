#!/bin/bash

rm -rf SMSWithoutBorders-App-Android
git clone git@github.com:smswithoutborders/SMSWithoutBorders-App-Android.git
cd SMSWithoutBorders-App-Android && \
	git checkout staging && \
	cp -v ../../release.properties . && \
	mkdir -p app/keys/ && \
	cp -v ../../app/keys/app-release-key.jks app/keys/ && \
	cp -v ../../ks.passwd . && \
	make clean && \
	python3 -m venv venv && \
	( \
	. venv/bin/activate && \
	pip install -r requirements.txt && \
	make release-cd status="draft")
