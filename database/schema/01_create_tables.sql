CREATE TABLE public.trainings (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  title text NOT NULL,
  description text,
  training_date date NOT NULL,
  created_by uuid,
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()),
  CONSTRAINT trainings_pkey PRIMARY KEY (id)
);

CREATE TABLE public.attendance (
  id uuid NOT NULL DEFAULT gen_random_uuid(),
  training_id uuid,
  player_id uuid,
  present boolean NOT NULL,
  created_at timestamp with time zone DEFAULT timezone('utc'::text, now()),
  CONSTRAINT attendance_pkey PRIMARY KEY (id),
  CONSTRAINT attendance_training_id_fkey
    FOREIGN KEY (training_id) REFERENCES public.trainings(id)
);

create table public.profiles (
  id uuid not null,
  role text not null default 'player'::text,
  created_at timestamp with time zone null default timezone ('utc'::text, now()),
  constraint profiles_pkey primary key (id),
  constraint profiles_id_fkey foreign KEY (id) references auth.users (id) on delete CASCADE,
  constraint profiles_role_check check (
    (
      role = any (array['player'::text, 'trainer'::text])
    )
  )
) TABLESPACE pg_default;
