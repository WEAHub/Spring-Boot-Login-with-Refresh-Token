# Spring Boot JWT
Ejemplo practico de como implementar un login con el uso de access/refresh tokens para la persistencia de la sesión.

## Endpoints

`POST` **/auth/login**
- JSON Body Request
```json
{
  "userName": "user",
  "password": "123"
}
```
- JSON Body Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzA1NDg0NTMwLCJleHAiOjE3MDU0ODYzMzB9.68Ca1uv5DxznSeIbkT751RUTX93dsE0s6LhawlC_kac",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzA1NDg0NTMwLCJleHAiOjE3MTMzNDMzMzB9.Vf3JswDfC1c1MFTFmHZ7pXmVPvxH7anJ0HvCM8IIKNs"
}
```
- Error devuelve `403 Forbidden`

- --

`POST` **/auth/addUser**
- JSON Body Request
```json
{
  "name": "admin",
  "password": "123",
  "email": "test@test.com",
  "roles": "ADMIN_ROLES"
}
```
- JSON Body Response
```json
{
  "message": "success"
}
```
- Error devuelve `403 Forbidden`


- --

`GET` **/auth/refresh**
- Hay que usar el refresh-token en la cabecera ***"Authorization"***
- JSON Body Response
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzA1NDg0NTMwLCJleHAiOjE3MDU0ODYzMzB9.68Ca1uv5DxznSeIbkT751RUTX93dsE0s6LhawlC_kac",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNzA1NDg0NTMwLCJleHAiOjE3MTMzNDMzMzB9.Vf3JswDfC1c1MFTFmHZ7pXmVPvxH7anJ0HvCM8IIKNs"
}
```
- Error devuelve `403 Forbidden`


- --

`GET` **/auth/getUsers**
- Solo un usuario con rol de admin puede listar todos los usuarios
- JSON Body Response
```json
[
  {
    "id": 1,
    "name": "user",
    "email": "test@test.com",
    "roles": "USER_ROLES",
    "password": "$2a$10$mFl7LkZJ.q4vC3cV9KuYs.BnwjhKzeGL3a13uCdMSxS7SmjZr7Zd."
  },
  {
    "id": 7,
    "name": "admin",
    "email": "test@test.com",
    "roles": "ADMIN_ROLES",
    "password": "$2a$10$jI20dRJY610ScPYBaTqR7uTU7iOOC8MRg8GHrPlW5zELbV2MjOhDi"
  },
]
```
- Error devuelve `403 Forbidden`


- --

`GET` **/auth/getUsers/{id}**
- Si el rol del usuario no es administrador solo podra listarse a si mismo.
- JSON Body Response
```json
[
  {
    "id": 1,
    "name": "user",
    "email": "test@test.com",
    "roles": "USER_ROLES",
    "password": "$2a$10$mFl7LkZJ.q4vC3cV9KuYs.BnwjhKzeGL3a13uCdMSxS7SmjZr7Zd."
  },
]
```
- Error devuelve `403 Forbidden`
