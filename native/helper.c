#define _GNU_SOURCE
#include <errno.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h>

int main(int argc, char **argv) {
    if (argc != 1) {
        fprintf(stderr, "usage: %s\n", argv[0]);
        return 64;
    }
    if (geteuid() != 0) {
        fputs("SUID required\n", stderr);
        return 77;
    }
    if (setresuid(0, 0, 0) != 0) {
        fprintf(stderr, "setresuid failed: %s\n", strerror(errno));
        return 77;
    }
    char *const environment[] = {"PATH=/system/bin:/vendor/bin", "LANG=C", NULL};
    char *const apply[] = {"/system/bin/setprop", "ro.boot.dbg_cnt", "1", NULL};
    execve("/system/bin/setprop", apply, environment);
    perror("execve /system/bin/setprop");
    return 126;
}
