Akka docker mesos consul
=========================

# Requirement
Vagrant
Virtualbox

# Setup
Add the following entries to your /etc/hosts file:
```
192.168.33.10	master
192.168.33.11	slave1
192.168.33.12	slave2
```

# Booting

Vagrant default password is: vagrant

Execute the following statements in your terminal:
```
cd deployment
vagrant up
ansible-playbook main.yml -i hosts -v -s -u vagrant -k
```

After the following services should be running: 
```
Marathon at http://master:8080
Mesos at http://master:5050
Consul at master:8500/ui/
```


