
TEXROOT := /usr/local/texlive/2010/bin/universal-darwin
LATEX := $(TEXROOT)/latex
DVIPDF := PATH="${PATH}:$(TEXROOT)" /usr/local/bin/dvipdf
BIBTEX := $(TEXROOT)/bibtex
MKGLO := PATH="${PATH}:$(TEXROOT)" $(TEXROOT)/makeglossaries

all:: Report.pdf

Report.pdf: *.tex
	$(LATEX) Report.tex
	$(MKGLO) Report
	$(LATEX) Report.tex
	$(MKGLO) Report
	$(BIBTEX) Report        # This is for the references in the main body
	$(BIBTEX) bu1           # This is for the appendix
	$(LATEX) Report.tex
	$(LATEX) Report.tex
	$(LATEX) Report.tex
	$(DVIPDF) Report.dvi    # Convert to a PDF
	@make dist-clean        # Get rid of the temp files.

open: Report.pdf
	open $<

dist-clean:
	-rm -f Report.log Report.dvi Report.toc Report.bbl Report.blg bu1.bbl bu1.blg
	-find . -name '*.aux' -exec rm -f '{}' \;

clean:
	-rm -f Report.pdf
