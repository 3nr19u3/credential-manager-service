create table if not exists users (
                                     id bigserial primary key,
                                     email varchar(255) unique not null,
    password_hash varchar(255) not null,
    created_at timestamptz not null default now()
    );

create type credential_status as enum ('PENDING','APPROVED','REJECTED');

create table if not exists credentials (
                                           id bigserial primary key,
                                           user_id bigint not null references users(id),
    type varchar(50) not null,
    issuer varchar(255) not null,
    license_number varchar(100) not null,
    expiry_date date not null,
    status credential_status not null default 'PENDING',
    deleted_at timestamptz null,
    created_at timestamptz not null default now()
    );

create index if not exists idx_credentials_user_created
    on credentials(user_id, created_at desc, id desc);

create index if not exists idx_credentials_user_status
    on credentials(user_id, status);

create index if not exists idx_credentials_user_type
    on credentials(user_id, type);
