execute populare_avion();
execute populare_cursa(1, 22, 'Bucuresti', 'Roma', TIMESTAMP'2016-12-01 10:00:00', 3);
execute populare_cursa(2, 22, 'Roma', 'Bucuresti', TIMESTAMP'2016-12-01 10:00:00', 3);
execute populare_cursa(3, 66, 'Londra', 'Bucuresti', TIMESTAMP'2016-12-01 12:00:00', 4);
execute populare_cursa(4, 66, 'Bucuresti', 'Londra', TIMESTAMP'2016-12-01 12:00:00', 4);
execute populare_cursa(5, 50, 'Iasi', 'Bucuresti', TIMESTAMP'2016-12-01 16:00:00', 1);
execute populare_cursa(6, 50, 'Bucuresti', 'Iasi', TIMESTAMP'2016-12-01 16:00:00', 1);
execute populare_cursa(7, 34, 'Iasi', 'Constanta', TIMESTAMP'2016-12-01 09:00:00', 1);
execute populare_cursa(8, 34, 'Constanta', 'Iasi', TIMESTAMP'2016-12-01 09:00:00', 1);

CREATE OR REPLACE PROCEDURE POPULARE_CURSA (v_idAvion in avion.id%TYPE, v_pret in cursa.pret%type, v_orasPlecare in cursa.oras_plecare%type,
v_orasSosire in cursa.oras_sosrire%type, v_data in cursa.d_plecare%type,
v_durata in integer)
IS
v_loc_ec avion.nr_locuri_ec%type;
v_loc_bs avion.nr_locuri_bs%type;
v_dPlecare cursa.d_plecare%type := v_data;
v_dSosire cursa.d_sosire%type := v_data + numtodsinterval(v_durata, 'hour');
v_index number := 1;
BEGIN
    select nr_locuri_ec, nr_locuri_bs into v_loc_ec, v_loc_bs from avion
    where avion.id = v_idAvion;
    while v_index < 366 loop
       v_dPlecare := v_dPlecare + interval '1' day;
       v_dSosire :=  v_dSosire + interval '1' day;
       insert  into cursa values (v_idAvion*366+v_index,
       v_dPlecare,
       v_dSosire,
       v_pret,
       v_orasPlecare,
       v_orasSosire,
       v_loc_ec,
       v_loc_bs,
       v_idAvion);
       v_index := v_index + 1;
    end loop;
END POPULARE_CURSA;

CREATE OR REPLACE PROCEDURE POPULARE_AVION 
IS 
BEGIN
  INSERT INTO avion values (1, 'A177B', 30, 6);
  INSERT INTO avion values (2, 'A177B', 30, 6);
  INSERT INTO avion values (3, 'A277B', 42, 12);
  INSERT INTO avion values (4, 'A277B', 42, 12);
  INSERT INTO avion values (5, 'A377B', 36, 6);
  INSERT INTO avion values (6, 'A377B', 36, 6);
  INSERT INTO avion values (7, 'A474B', 60, 18);
  INSERT INTO avion values (8, 'A474B', 60, 18);
END POPULARE_AVION;