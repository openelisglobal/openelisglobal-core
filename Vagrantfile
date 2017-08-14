# -*- mode: ruby -*-
# vi: set ft=ruby :

# Vagrantfile API/syntax version. Don't touch unless you know what you're doing!
VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "bento/ubuntu-16.04"
  config.vm.hostname = "oe"
  
  # forward http
  config.vm.network "forwarded_port", host: 62326, guest: 80
  
  # forward https
  config.vm.network "forwarded_port", host: 62443, guest: 443
  
  # forward postgres
  config.vm.network "forwarded_port", host: 5432, guest: 5432
  
  config.vm.provision :shell, :inline => "sudo apt-get update -y", run: "once"
  config.vm.provision :shell, :inline => "sudo apt-get install -y puppet python python-yaml", run: "once"
  config.vm.provision :shell, :inline => "sudo git clone --recursive https://github.com/I-TECH-UW/appliance-setup.git /opt/appliance-setup", run: "once"
  config.vm.provision :shell, :inline => "sudo APPLIANCE_COMPONENTS=\"openelis\" /opt/appliance-setup/bin/appliance-setup apply", run: "once"

  config.vm.provider "virtualbox" do |v|
	v.memory = 1024
	v.cpus = 2
  end
end
