INSERT INTO public.role (id, name)
VALUES (1, 'ROLE_USER')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO public.role (id, name)
VALUES (2, 'ROLE_ADMIN')
    ON CONFLICT (id) DO NOTHING;

INSERT INTO public.role (id, name)
VALUES (3, 'ROLE_MODERATOR')
    ON CONFLICT (id) DO NOTHING;

ALTER TABLE simple_message
ALTER COLUMN message TYPE VARCHAR(4096);

ALTER TABLE client
    add unique (phone);