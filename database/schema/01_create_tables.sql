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
