#!/bin/sh
echo Start app
cd backend/
WORKERS_NUMBER="${WORKERS_NUMBER:-4}"
sleep 5
echo Run migrate
python manage.py migrate
echo Create user ${SUPERUSER} with email ${SUPERUSER_EMAIL} and password ${SUPERUSER_PASSWORD}
python manage.py create_superuser_with_password --noinput --username ${SUPERUSER} --email ${SUPERUSER_EMAIL} --password ${SUPERUSER_PASSWORD} --preserve
python manage.py collectstatic --noinput
echo Run server
gunicorn -t 600 -b 0.0.0.0:8000 --workers="$WORKERS_NUMBER" --log-level debug --capture-output app.wsgi:application
