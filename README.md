# Identity Service
## Назначение

Сервис отвечает за аутентификацию и авторизацию пользователей.

Таблицы

users

id UUID PK
email VARCHAR UNIQUE NOT NULL
password_hash TEXT NOT NULL
status VARCHAR NOT NULL -- ACTIVE / BLOCKED
created_at TIMESTAMP NOT NULL
updated_at TIMESTAMP NOT NULL


roles

id UUID PK
name VARCHAR UNIQUE -- USER, ADMIN


user_roles

user_id UUID
role_id UUID
PRIMARY KEY (user_id, role_id)


refresh_tokens

id UUID PK
user_id UUID
token TEXT
expires_at TIMESTAMP
created_at TIMESTAMP

