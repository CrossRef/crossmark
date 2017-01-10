set -e 

docker-compose run -w /usr/src/app test lein test

docker-compose run -w /usr/src/app test lein eastwood

grep 'TODO' -r src
if [ $? -eq 0 ]; then false; fi;
