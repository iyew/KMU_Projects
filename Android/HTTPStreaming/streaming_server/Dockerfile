FROM php:7.0-apache
RUN docker-php-ext-install mysqli
EXPOSE 80 3306
COPY ./www /var/www/html
RUN chmod a+x /var/www/html
