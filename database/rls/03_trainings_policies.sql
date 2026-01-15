-- Enable RLS
ALTER TABLE public.trainings ENABLE ROW LEVEL SECURITY;

-- Everyone can view trainings
ALTER POLICY "Everyone can view trainings"
ON public.trainings
TO public
USING (
  true
);

-- Users can insert their own trainings
ALTER POLICY "Users can insert their own trainings"
ON public.trainings
TO authenticated
WITH CHECK (
  auth.uid() = created_by
);

-- Users can delete their own trainings
ALTER POLICY "Users can delete their own trainings"
ON public.trainings
TO authenticated
USING (
  auth.uid() = created_by
);

alter policy "Trainers can delete trainings"
on "public"."trainings"
to authenticated
using (
  (EXISTS ( SELECT 1
   FROM profiles
  WHERE ((profiles.id = auth.uid()) AND (profiles.role = 'trainer'::text))))
);

alter policy "Trainers can insert trainings"
on "public"."trainings"
to authenticated
using (
) with check (
  (EXISTS ( SELECT 1
   FROM profiles
  WHERE ((profiles.id = auth.uid()) AND (profiles.role = 'trainer'::text))))

);
