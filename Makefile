.PHONY: test-allure allure-report allure-open allure-serve clean-allure copy-history

test-allure:
	mvn clean test

copy-history:
	@if [ -d allure-report/history ]; then mkdir -p allure-results/history && cp -R allure-report/history/. allure-results/history/; fi

allure-report: copy-history
	npx allure-commandline generate allure-results --clean -o allure-report

allure-open:
	npx allure-commandline open allure-report

allure-serve:
	npx allure-commandline serve allure-results

clean-allure:
	rm -rf allure-results allure-report
