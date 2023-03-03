# update packages
sudo apt update

# install parted
sudo apt install parted

# partitioning the EBS drive that will be used for backup (nvme1n1)
sudo parted /dev/nvme1n1
(parted) select /dev/nvme1n1
(parted) mklabel gpt
(parted) mkpart primary ext4 1MB 3000GB
(parted) quit

# formatting the partition
sudo mkfs -t ext4 /dev/nvme1n1p1

# mounting the drive
mkdir /home/admin/backup
sudo mount -o rw /dev/nvme1n1p1 /home/admin/backup

# mounting the mongodb snapshot
sudo mkdir /srv/mongodb
sudo mount -o rw /dev/nvme0n1p1 /srv/mongodb
cd /srv
sudo chown -R mongodb .
sudo chgrp -R mongodb .

# installing mongodb
sudo apt-get install gnupg
wget -qO - https://www.mongodb.org/static/pgp/server-4.2.asc | sudo apt-key add -
echo "deb http://repo.mongodb.org/apt/debian buster/mongodb-org/4.2 main" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.2.list
sudo apt-get update
sudo apt-get install -y mongodb-org

# loading the mongodb snapshot
# it may take a while (over 5 min) to fully load it
mkdir /home/admin/process
cd ~/process
nohup sh -c "sudo mongod --dbpath /srv/mongodb/" &

# exporting data
cd ~
nohup sh -c "sudo mongoexport --collection=my_collection --db=my_database --out=/home/admin/backup/file.json" &
