SRCDIR      := src
DEPDIR      := deps
BUILDDIR    := build
RESOURCEDIR := resources
OUT         := PriorityListPlugin.jar
MAIN        := org/usfirst/frc/team1923/shuffleboard/PriorityListPlugin.java

$(OUT): FORCE
	rm -rf $(BUILDDIR)
	mkdir -p $(BUILDDIR)
	javac -cp $(DEPDIR)/*:$(SRCDIR) $(SRCDIR)/$(MAIN) -d $(BUILDDIR)
	cp -r  $(RESOURCEDIR)/* $(BUILDDIR)
	jar cf $(OUT) -C $(BUILDDIR) .
	rm -rf $(BUILDDIR)

.PHONY: FORCE
FORCE: