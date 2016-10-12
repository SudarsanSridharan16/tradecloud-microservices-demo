Playing with Akka, Docker, Mesos, Consul
========================================

# Requirement
* Vagrant
* Ansible
* VirtualBox
* Docker Hub repository

## Setup

Add the following entries to your /etc/hosts file:
```
192.168.33.10	master
192.168.33.11	slave1 
192.168.33.12	slave2 
```

## Create the environment

Vagrant default password is: vagrant

Execute the following statements in your terminal:

```
cd deployment
vagrant up
ansible-playbook main.yml -i hosts -v -s -u vagrant -k
ansible-playbook endpoint.yml -i hosts -v -s -u vagrant -k --tags frontendApp
```

After the following services should be running:
 
```
Marathon at http://master:8080
Mesos at http://master:5050
Consul at master:8500/ui/
```

# Build (if you want to build you own Docker image)

Adjust the following settings:

In the build.sbt file adjust the repository:

```
dockerRepository := Some("your docker hub repository")
```

Build and publish the docker image to Docker Hub by running:

```
sbt docker:publish
```

When your repository at Docker Hub requires authentication, add the file /deployment/group_vars/main.yml with the following contents:

```
dockerhub:
  username: "your dockerhub username"
  password: "your dockerhub password"
  email: "your dockerhub email"
```

# Deploy

Adjust the following entries in the file /deployment/group_vars/all.yml (if you use your own docker image) 

```
app:
  name: akkadocker
  image_name: benniekrijger/akkadocker
  exposed_port: 8080
```

Deploy the app to Marathon by running:

```
bash bin/deploy.sh
```

If everything is ok you will see the App getting deployed in Marathon (http://master:8080)

After deployment the API of the app is accessible at: http://master/