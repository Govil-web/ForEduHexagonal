```ascii
classDiagram
direction BT
class academic_terms {
organization_id
name
start_date
end_date
id
}
class announcements {
organization_id
author_user_id
course_id
title
content
created_at
is_pinned
id
}
class assignments {
course_id
title
description
created_at
due_date
max_points
id
}
class attendance_records {
enrollment_id
session_date
status
notes
id
}
class audit_log {
organization_id
user_id
action
entity_type
entity_id
details
timestamp
id
}
class courses {
subject_id
academic_term_id
teacher_user_id
course_code
id
}
class enrollments {
student_user_id
course_id
enrollment_date
status
final_grade
id
}
class fee_templates {
organization_id
name
description
default_amount
is_active
id
}
class fees {
student_user_id
template_id
description
amount
due_date
status
created_at
id
}
class flyway_schema_history {
version
description
type
script
checksum
installed_by
installed_on
execution_time
success
installed_rank
}
class forum_posts {
thread_id
author_user_id
content
created_at
parent_post_id
id
}
class forum_threads {
course_id
created_by_user_id
title
created_at
is_locked
id
}
class grades {
submission_id
enrollment_id
grade
graded_at
feedback_from_teacher
teacher_user_id
id
}
class guardian_profiles {
occupation
is_financial_responsible
user_id
}
class organizations {
uuid
name
subdomain
is_active
digital_consent_age
created_at
updated_at
id
}
class payments {
fee_id
paid_by_user_id
amount
payment_date
method
transaction_id
notes
id
}
class roles {
name
scope
id
}
class staff_profiles {
employee_id_number
hire_date
department
title
user_id
}
class student_guardian_relationships {
student_user_id
guardian_user_id
relationship_type
is_primary_contact
id
}
class student_profiles {
organization_id
student_id_number
enrollment_date
current_grade_level
observations
user_id
}
class subjects {
organization_id
name
subject_code
grade_level
description
is_active
id
}
class submissions {
assignment_id
enrollment_id
submitted_at
content
notes_from_student
id
}
class user_roles {
user_id
role_id
}
class users {
organization_id
first_name
last_name
dni
email
password_hash
birth_date
phone_number
account_status
created_at
updated_at
id
}

academic_terms  -->  organizations : organization_id:id
announcements  -->  courses : course_id:id
announcements  -->  organizations : organization_id:id
announcements  -->  users : author_user_id:id
assignments  -->  courses : course_id:id
attendance_records  -->  enrollments : enrollment_id:id
audit_log  -->  organizations : organization_id:id
audit_log  -->  users : user_id:id
courses  -->  academic_terms : academic_term_id:id
courses  -->  staff_profiles : teacher_user_id:user_id
courses  -->  subjects : subject_id:id
enrollments  -->  courses : course_id:id
enrollments  -->  student_profiles : student_user_id:user_id
fee_templates  -->  organizations : organization_id:id
fees  -->  fee_templates : template_id:id
fees  -->  student_profiles : student_user_id:user_id
forum_posts  -->  forum_posts : parent_post_id:id
forum_posts  -->  forum_threads : thread_id:id
forum_posts  -->  users : author_user_id:id
forum_threads  -->  courses : course_id:id
forum_threads  -->  users : created_by_user_id:id
grades  -->  enrollments : enrollment_id:id
grades  -->  staff_profiles : teacher_user_id:user_id
grades  -->  submissions : submission_id:id
guardian_profiles  -->  users : user_id:id
payments  -->  fees : fee_id:id
payments  -->  users : paid_by_user_id:id
staff_profiles  -->  users : user_id:id
student_guardian_relationships  -->  guardian_profiles : guardian_user_id:user_id
student_guardian_relationships  -->  student_profiles : student_user_id:user_id
student_profiles  -->  organizations : organization_id:id
student_profiles  -->  users : user_id:id
subjects  -->  organizations : organization_id:id
submissions  -->  assignments : assignment_id:id
submissions  -->  enrollments : enrollment_id:id
user_roles  -->  roles : role_id:id
user_roles  -->  users : user_id:id
users  -->  organizations : organization_id:id
```
