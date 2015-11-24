
All the tests share some common configuration stored as property files
in this directory.

These configure a scratch database connection for use by the tests.
The database is cleared after each test.

Additional database and configuration fixtures can be specified per-test using
the annotations in uk.ac.ed.epcc.webapp.junit4

Note that database features are loaded using the XML dump/undump feature.
There is also an older mechanism that stores the fixtures in a secondary database but 
that is much harder to keep synchronized between multiple developers.

