## basic commands ##
# start docker service
sudo systemctl start docker

# build docker image from Dockerfile
docker build -t image_name .

# list all existing containers
docker container ls -a

# clear all images
docker image prune -a

# clear all containers
docker container prune -a

# clear everything
docker system prune -a

# Getting inside a container via bash
docker run -it --entrypoint /bin/bash python_container

## creating a dev env using docker ##
# -v makes a local dir visible to the container: local_path:container_path
docker run -v /home/username/Projects:/Projects --name python_dev -dti python:latest

# if you need to dynamically expose ports, you can pass the --network="host" parameter
docker run -v /home/username/Projects:/Projects --network="host" --name python_dev -dti python:latest

# you may need to specify a different entrypoint if your docker image doesn't point to bash by default
docker run -v /home/username/Projects:/Projects --name python_dev --entrypoint /bin/bash -dti python:latest

# if you want to run a web app or service, don't forget to pass the port and IP address
docker run -p 127.0.0.1:8787:8787 --name rstudio_dev -e DISABLE_AUTH=true rocker/verse:latest

# AWS Lambda docker images require the entrypoint to be passed before other arguments
docker run -it --entrypoint /bin/bash --name lambda_dev -dti amazon/aws-lambda-provided:latest
