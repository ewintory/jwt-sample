# https://github.com/bitnami/bitnami-docker-tomcat
FROM bitnami/tomcat:9.0-debian-10

USER root

ENV DEBIAN_FRONTEND=noninteractive

RUN set -eux; \
  apt-get update; \
  apt-get -yq --no-install-recommends install \
    build-essential \
    ca-certificates \
    curl \
    wget \
    msmtp \
    git \
    bash-completion \
    less \
    jq \
    tzdata \
    locales \
    dnsutils \
    net-tools \
    netcat \
    lsof \
    strace \
    procps \
    vim \
    nano \
    htop \
  ; \
  apt-get clean; \
  rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*; \
  ln -s /usr/bin/msmtp /usr/sbin/sendmail;

RUN set -eux; \
  apt-get update; \
  apt-get -yq --no-install-recommends install \
    libcap2-bin \
    apache2 \
    libapache2-mod-rpaf \
    libapache2-mod-jk \
  ; \
  apt-get clean; \
  rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*;

# Files must be copied before "chmod -R a+rwx /opt/bitnami/tomcat"
# as server.xml is modified in place
COPY layer /

RUN echo 'web:x:10000:' >>/etc/group
RUN echo 'web:x:10000:10000:Web User:/home/user:/bin/bash' >>/etc/passwd
RUN install -d /home/user -o 10000 -g 10000 -m 0700
RUN chmod a+rw /etc/msmtprc

RUN set -eux; \
  a2disconf other-vhosts-access-log; \
  a2enmod proxy; \
  a2enmod proxy_ajp; \
  a2enmod remoteip; \
  a2enmod rewrite; \
  a2enmod expires; \
  a2enmod headers; \
  chmod -R a+rwx /opt/bitnami/tomcat; \
  install -d /a/shared/public -o 10000 -g 10000; \
  install -d /a/conf -o 10000 -g 10000; \
  install -d /run/apache2 -o 10000;

USER 10000
ENV APACHE_RUN_USER="web"
ENV APACHE_RUN_GROUP="web"

ENV TOMCAT_AJP_PORT_NUMBER=8009
ENV TOMCAT_HTTP_PORT_NUMBER=8081
ENV TOMCAT_ALLOW_REMOTE_MANAGEMENT=1
ENV TOMCAT_USERNAME=user
ENV TOMCAT_PASSWORD=password

COPY build/libs/jwt-sample.war /opt/bitnami/tomcat/webapps_default

ENTRYPOINT [ "/entrypoint" ]