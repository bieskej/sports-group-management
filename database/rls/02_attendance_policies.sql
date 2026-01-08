-- Enable RLS
ALTER TABLE public.attendance ENABLE ROW LEVEL SECURITY;

-- Users can view their own attendance
ALTER POLICY "Users can view own attendance"
ON public.attendance
TO public
USING (
  auth.uid() = player_id
);
