FROM solr:8.7.0

COPY common-utilities/build/solrdist /opt/solr/server/solr-webapp/webapp/WEB-INF/lib
COPY initialization-stage/build/solrdist /opt/solr/server/solr-webapp/webapp/WEB-INF/lib
COPY conceptualization-stage/build/solrdist /opt/solr/server/solr-webapp/webapp/WEB-INF/lib
COPY linguistic-expansion-stage/build/solrdist /opt/solr/server/solr-webapp/webapp/WEB-INF/lib
COPY query-preparation-stage/build/solrdist /opt/solr/server/solr-webapp/webapp/WEB-INF/lib
COPY comprehension-stage/build/solrdist /opt/solr/server/solr-webapp/webapp/WEB-INF/lib
COPY spellcheck-stage/build/solrdist /opt/solr/server/solr-webapp/webapp/WEB-INF/lib
COPY weighting-stage/build/solrdist /opt/solr/server/solr-webapp/webapp/WEB-INF/lib
COPY sqp-boosting-strategy/build/solrdist /opt/solr/server/solr-webapp/webapp/WEB-INF/lib
