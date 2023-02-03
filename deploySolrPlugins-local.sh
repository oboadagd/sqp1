./gradlew clean prepareSolrExtensions

cp ./initialization-stage/build/solrdist/* /opt/solr-8.7.0/server/solr-webapp/webapp/WEB-INF/lib/
cp ./conceptualization-stage/build/solrdist/* /opt/solr-8.7.0/server/solr-webapp/webapp/WEB-INF/lib/
cp ./common-utilities/build/solrdist/* /opt/solr-8.7.0/server/solr-webapp/webapp/WEB-INF/lib/
cp ./linguistic-expansion-stage/build/solrdist/* /opt/solr-8.7.0/server/solr-webapp/webapp/WEB-INF/lib/
cp ./query-preparation-stage/build/solrdist/* /opt/solr-8.7.0/server/solr-webapp/webapp/WEB-INF/lib/
cp ./spellcheck-stage/build/solrdist/* /opt/solr-8.7.0/server/solr-webapp/webapp/WEB-INF/lib/
cp ./sqp-boosting-strategy/build/solrdist/* /opt/solr-8.7.0/server/solr-webapp/webapp/WEB-INF/lib/

cd /opt/solr-8.7.0

# for standalone solr
./bin/solr stop -all
./bin/solr start -a "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=18983"

# for solr cloud
#./bin/solr stop -all
#./bin/solr start -cloud -p 8983 -s "example/cloud/node1/solr" -a "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=18983"
#./bin/solr start -cloud -p 8982 -s "example/cloud/node2/solr" -z localhost:9983