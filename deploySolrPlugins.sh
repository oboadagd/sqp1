./gradlew clean build prepareSolrExtensions

DOCKERHUB_LOGIN=<Your dockerhub login>
DOCKERHUB_REPO=<Your dockerhub repo>
DOCKERHUB_TAG=<incremental version number>

docker build -t ${DOCKERHUB_LOGIN}/${DOCKERHUB_REPO}:${DOCKERHUB_TAG} ./

docker login
docker push ${DOCKERHUB_LOGIN}/${DOCKERHUB_REPO}:${DOCKERHUB_TAG}

kubectl config use-context gke_lw-sales_us-west1-a_partner-cluster
kubectl set image deployment/grid-dynamics-solr-exporter solr-init=${DOCKERHUB_LOGIN}/${DOCKERHUB_REPO}:${DOCKERHUB_TAG} --record
kubectl set image deployment/grid-dynamics-solr-exporter exporter=${DOCKERHUB_LOGIN}/${DOCKERHUB_REPO}:${DOCKERHUB_TAG} --record
kubectl set image statefulset/grid-dynamics-solr solr=${DOCKERHUB_LOGIN}/${DOCKERHUB_REPO}:${DOCKERHUB_TAG} --record
