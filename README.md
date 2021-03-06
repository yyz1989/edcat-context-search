edcat-context-search
====================

### Introduction

The search capability of EDCAT is further powered by an external linked data based text analytics application conTEXT. Given a text corpus such as articles, blogs, tweets or RSS feeds, conTEXT analyzes it semantically and tags any contextual named entities discovered in the text. For example, if a blog talks about “linked data”, it will discover this keyword phrase and count its occurrence. By virtue of this feature, EDCAT is capable of performing an in-depth context search to filter all the datasets related to given named entities through the context search plugin. Intuitively, a user performing a text analysis is probably interested in some further knowledge related to this text. Given the named entities discovered by conTEXT, the context search plugin provides users with a list of datasets matching against at least one named entity ordered by the number of named entities tagged on the same dataset. Consequently, users may continue browsing the most relevant datasets.

### Attention

This plugin has to be used together with EDCAT core. For more information related to EDCAT, please visit http://edcat.tenforce.com and https://github.com/tenforce/edcat .
